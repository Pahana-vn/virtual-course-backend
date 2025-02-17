package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.ECourseLevel;
import com.mytech.virtualcourse.enums.ECourseStatus;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.*;
import com.mytech.virtualcourse.repositories.*;
import com.mytech.virtualcourse.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private StudentLectureProgressRepository studentLectureProgressRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private LectureMapper lectureMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerOptionMapper answerOptionMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(course -> {
                    CourseDTO dto = courseMapper.courseToCourseDTO(course);
                    if (course.getImageCover() != null) {

                        dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());

                        // Thêm URL đầy đủ cho instructor photo
                        if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                            dto.getInstructorInfo().setPhoto("http://localhost:8080/uploads/instructor/" + course.getInstructor().getPhoto());
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.getSections().forEach(section -> {
            section.getLectures().forEach(lecture -> {
                lecture.getArticles().size();
            });
        });

        CourseDTO dto = courseMapper.courseToCourseDTO(course);
        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
            if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                dto.getInstructorInfo().setPhoto("http://localhost:8080/uploads/instructor/" + course.getInstructor().getPhoto());
            }
        }
        return dto;
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        // Kiểm tra đầu vào
        if (courseDTO.getInstructorId() == null || courseDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("Instructor ID and Category ID are required.");
        }

        // Tìm Instructor và Category
        Instructor instructor = instructorRepository.findById(courseDTO.getInstructorId())
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found."));
        Category category = categoryRepository.findById(courseDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found."));

        // Ánh xạ từ CourseDTO sang Course
        Course course = courseMapper.courseDTOToCourse(courseDTO);

        // Thiết lập thông tin bổ sung
        course.setInstructor(instructor);
        course.setCategory(category);

        // Xử lý các Section, Lectures, Articles như trước
        if (courseDTO.getSections() != null && !courseDTO.getSections().isEmpty()) {
            List<Section> sections = courseDTO.getSections().stream()
                    .map(sectionDTO -> {
                        Section section = sectionMapper.sectionDTOToSection(sectionDTO);
                        section.setCourse(course);

                        if (sectionDTO.getLectures() != null && !sectionDTO.getLectures().isEmpty()) {
                            List<Lecture> lectures = sectionDTO.getLectures().stream()
                                    .map(lectureDTO -> {
                                        Lecture lecture = lectureMapper.lectureDTOToLecture(lectureDTO);
                                        lecture.setSection(section);

                                        if (lectureDTO.getArticles() != null && !lectureDTO.getArticles().isEmpty()) {
                                            List<Article> articles = lectureDTO.getArticles().stream()
                                                    .map(articleDTO -> {
                                                        Article article = articleMapper.articleDTOToArticle(articleDTO);
                                                        article.setLecture(lecture);
                                                        return article;
                                                    })
                                                    .toList();
                                            lecture.setArticles(articles);
                                        }

                                        return lecture;
                                    })
                                    .toList();
                            section.setLectures(lectures);
                        }

                        return section;
                    })
                    .toList();
            course.setSections(sections);
        }

        // Xử lý danh sách Questions trong Course
        if (courseDTO.getQuestions() != null && !courseDTO.getQuestions().isEmpty()) {
            List<Question> questions = courseDTO.getQuestions().stream()
                    .map(questionDTO -> {
                        Question question = questionMapper.questionDTOToQuestion(questionDTO);
                        question.setCourse(course);

                        // Xử lý danh sách AnswerOptions trong Question
                        if (questionDTO.getAnswerOptions() != null && !questionDTO.getAnswerOptions().isEmpty()) {
                            List<AnswerOption> answerOptions = questionDTO.getAnswerOptions().stream()
                                    .map(answerOptionDTO -> {
                                        AnswerOption answerOption = answerOptionMapper.answerOptionDTOToAnswerOption(answerOptionDTO);
                                        answerOption.setQuestion(question);
                                        return answerOption;
                                    })
                                    .toList();
                            question.setAnswerOptions(answerOptions);
                        }

                        return question;
                    })
                    .toList();
            course.setQuestions(questions);
        }

        // Lưu khóa học vào cơ sở dữ liệu
        Course savedCourse = courseRepository.save(course);
        return courseMapper.courseToCourseDTO(savedCourse);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        // Tìm Course hiện tại
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        AtomicBoolean isUpdated = new AtomicBoolean(false);

        // Cập nhật thông tin cơ bản của Course
        if (!existingCourse.getTitleCourse().equals(courseDTO.getTitleCourse())) {
            existingCourse.setTitleCourse(courseDTO.getTitleCourse());
            isUpdated.set(true);
        }
        if (!existingCourse.getDescription().equals(courseDTO.getDescription())) {
            existingCourse.setDescription(courseDTO.getDescription());
            isUpdated.set(true);
        }
        if (!existingCourse.getBasePrice().equals(courseDTO.getBasePrice())) {
            existingCourse.setBasePrice(courseDTO.getBasePrice());
            isUpdated.set(true);
        }
        if (!existingCourse.getDuration().equals(courseDTO.getDuration())) {
            existingCourse.setDuration(courseDTO.getDuration());
            isUpdated.set(true);
        }
        if (!existingCourse.getImageCover().equals(courseDTO.getImageCover())) {
            existingCourse.setImageCover(courseDTO.getImageCover());
            isUpdated.set(true);
        }
        if (!existingCourse.getUrlVideo().equals(courseDTO.getUrlVideo())) {
            existingCourse.setUrlVideo(courseDTO.getUrlVideo());
            isUpdated.set(true);
        }

        // Cập nhật level (enum)
        if (courseDTO.getLevel() != null && !existingCourse.getLevel().name().equalsIgnoreCase(courseDTO.getLevel().toString())) {
            try {
                ECourseLevel level = ECourseLevel.valueOf(courseDTO.getLevel().toString().toUpperCase());
                existingCourse.setLevel(level);
                isUpdated.set(true);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid level value: " + courseDTO.getLevel());
            }
        }

        // Cập nhật status (enum)
        if (courseDTO.getStatus() != null && !existingCourse.getStatus().name().equalsIgnoreCase(courseDTO.getStatus().toString())) {
            try {
                ECourseStatus status = ECourseStatus.valueOf(courseDTO.getStatus().toString().toUpperCase());
                existingCourse.setStatus(status);
                isUpdated.set(true);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + courseDTO.getStatus());
            }
        }

        // Cập nhật Instructor
        if (courseDTO.getInstructorId() != null && (existingCourse.getInstructor() == null || !existingCourse.getInstructor().getId().equals(courseDTO.getInstructorId()))) {
            Instructor instructor = instructorRepository.findById(courseDTO.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + courseDTO.getInstructorId()));
            existingCourse.setInstructor(instructor);
            isUpdated.set(true);
        }

        // Cập nhật Category
        if (courseDTO.getCategoryId() != null && (existingCourse.getCategory() == null || !existingCourse.getCategory().getId().equals(courseDTO.getCategoryId()))) {
            Category category = categoryRepository.findById(courseDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + courseDTO.getCategoryId()));
            existingCourse.setCategory(category);
            isUpdated.set(true);
        }

        // Cập nhật Sections và các đối tượng liên quan
        if (courseDTO.getSections() != null) {
            // Lấy danh sách ID của Sections từ DTO
            List<Long> sectionIdsFromDTO = courseDTO.getSections().stream()
                    .map(SectionDTO::getId)
                    .filter(Objects::nonNull)
                    .toList();

            // Xóa các Sections không còn trong DTO
            existingCourse.getSections().removeIf(existingSection -> {
                boolean toBeRemoved = !sectionIdsFromDTO.contains(existingSection.getId());
                if (toBeRemoved) {
                    // Kiểm tra nếu `progress` tồn tại
                    if (!existingSection.getProgress().isEmpty()) {
                        throw new IllegalStateException("Cannot delete section with existing progress records.");
                    }

                    sectionRepository.delete(existingSection); // Xóa Section khỏi database
                    isUpdated.set(true); // Đánh dấu khi có thay đổi
                }
                return toBeRemoved; // Xóa khỏi danh sách trong bộ nhớ
            });
            for (SectionDTO sectionDTO : courseDTO.getSections()) {
                Section existingSection;

                // Nếu `sectionDTO.getId()` là null, tạo mới Section
                if (sectionDTO.getId() == null) {
                    existingSection = new Section();
                    existingSection.setCourse(existingCourse); // Gán Course hiện tại
                    existingCourse.getSections().add(existingSection); // Thêm vào danh sách Sections
                    isUpdated.set(true); // Đánh dấu khi thêm mới
                } else {
                    // Tìm Section dựa trên ID nếu đã tồn tại
                    existingSection = existingCourse.getSections().stream()
                            .filter(sec -> sec.getId().equals(sectionDTO.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                Section newSection = new Section();
                                newSection.setCourse(existingCourse);
                                existingCourse.getSections().add(newSection);
                                isUpdated.set(true); // Đánh dấu khi thêm mới
                                return newSection;
                            });
                }

                // Cập nhật thông tin của Section
                if (!Objects.equals(existingSection.getTitleSection(), sectionDTO.getTitleSection())) {
                    existingSection.setTitleSection(sectionDTO.getTitleSection());
                    isUpdated.set(true);
                }
                List<Long> lectureIdsFromDTO = sectionDTO.getLectures().stream()
                        .map(LectureDTO::getId)
                        .filter(Objects::nonNull)
                        .toList();

                // Xóa các Lectures không còn trong DTO
                existingSection.getLectures().removeIf(existingLecture -> {
                    boolean toBeRemoved = !lectureIdsFromDTO.contains(existingLecture.getId());
                    if (toBeRemoved) {
                        // Xóa các Articles liên quan đến Lecture trước khi xóa Lecture
                        articleRepository.deleteAll(existingLecture.getArticles());
                        lectureRepository.delete(existingLecture); // Xóa Lecture khỏi database
                        isUpdated.set(true); // Đánh dấu khi có thay đổi
                    }
                    return toBeRemoved;
                });

                // Cập nhật hoặc thêm mới Lectures từ DTO
                for (LectureDTO lectureDTO : sectionDTO.getLectures()) {
                    Lecture existingLecture;

                    // Nếu `lectureDTO.getId()` là null, tạo mới Lecture
                    if (lectureDTO.getId() == null) {
                        existingLecture = new Lecture();
                        existingLecture.setSection(existingSection); // Gán Section hiện tại
                        existingSection.getLectures().add(existingLecture); // Thêm vào danh sách Lectures
                        isUpdated.set(true); // Đánh dấu khi thêm mới
                    } else {
                        // Tìm Lecture dựa trên ID nếu đã tồn tại
                        existingLecture = existingSection.getLectures().stream()
                                .filter(lec -> lec.getId().equals(lectureDTO.getId()))
                                .findFirst()
                                .orElseGet(() -> {
                                    Lecture newLecture = new Lecture();
                                    newLecture.setSection(existingSection);
                                    existingSection.getLectures().add(newLecture);
                                    isUpdated.set(true); // Đánh dấu khi thêm mới
                                    return newLecture;
                                });
                    }

                    // Cập nhật thông tin của Lecture
                    if (!Objects.equals(existingLecture.getTitleLecture(), lectureDTO.getTitleLecture())) {
                        existingLecture.setTitleLecture(lectureDTO.getTitleLecture());
                        isUpdated.set(true);
                    }

                    // Cập nhật video của Lecture
                    if (!Objects.equals(existingLecture.getLectureVideo(), lectureDTO.getLectureVideo())) {
                        existingLecture.setLectureVideo(lectureDTO.getLectureVideo());
                        isUpdated.set(true);
                    }

                    // Cập nhật hoặc thêm mới Articles
                    if (lectureDTO.getArticles() != null) {
                        // Lấy danh sách ID của Articles từ DTO
                        List<Long> articleIdsFromDTO = lectureDTO.getArticles().stream()
                                .map(ArticleDTO::getId)
                                .filter(Objects::nonNull)
                                .toList();

                        // Xóa các Articles không còn trong DTO
                        existingLecture.getArticles().removeIf(existingArticle -> {
                            boolean toBeRemoved = !articleIdsFromDTO.contains(existingArticle.getId());
                            if (toBeRemoved) {
                                // Nếu cần, bạn có thể gọi repository để xóa trực tiếp trong database
                                articleRepository.delete(existingArticle);
                            }
                            return toBeRemoved;
                        });

                        // Cập nhật hoặc thêm mới các Articles từ DTO
                        for (ArticleDTO articleDTO : lectureDTO.getArticles()) {
                            Article existingArticle;

                            // Nếu `articleDTO.getId()` là null, tạo mới Article
                            if (articleDTO.getId() == null) {
                                existingArticle = new Article();
                                existingArticle.setLecture(existingLecture); // Gán Lecture hiện tại
                                existingLecture.getArticles().add(existingArticle); // Thêm vào danh sách Articles
                                isUpdated.set(true); // Đánh dấu khi thêm mới
                            } else {
                                // Tìm Article dựa trên ID nếu đã tồn tại
                                existingArticle = existingLecture.getArticles().stream()
                                        .filter(art -> art.getId().equals(articleDTO.getId()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            Article newArticle = new Article();
                                            newArticle.setLecture(existingLecture);
                                            existingLecture.getArticles().add(newArticle);
                                            isUpdated.set(true); // Đánh dấu khi thêm mới
                                            return newArticle;
                                        });
                            }

                            // Cập nhật thông tin của Article
                            if (!Objects.equals(existingArticle.getContent(), articleDTO.getContent())) {
                                existingArticle.setContent(articleDTO.getContent());
                                isUpdated.set(true);
                            }
                            if (!Objects.equals(existingArticle.getFileUrl(), articleDTO.getFileUrl())) {
                                existingArticle.setFileUrl(articleDTO.getFileUrl());
                                isUpdated.set(true);
                            }
                        }
                    }
                }
            }
        }

        // Cập nhật Questions và AnswerOptions
        if (courseDTO.getQuestions() != null) {
            // Lấy danh sách ID của Questions từ DTO
            List<Long> questionIdsFromDTO = courseDTO.getQuestions().stream()
                    .map(QuestionDTO::getId)
                    .filter(Objects::nonNull)
                    .toList();

            // Xóa các Questions không còn trong DTO
            existingCourse.getQuestions().removeIf(existingQuestion -> {
                boolean toBeRemoved = !questionIdsFromDTO.contains(existingQuestion.getId());
                if (toBeRemoved) {
                    // Kiểm tra nếu có tham chiếu từ Test
                    if (existingQuestion.getTests() != null && !existingQuestion.getTests().isEmpty()) {
                        throw new IllegalStateException("Cannot delete question because it is being used in one or more tests.");
                    }
                    // Kiểm tra nếu `studentAnswers` tồn tại
                    if (!existingQuestion.getStudentAnswers().isEmpty()) {
                        throw new IllegalStateException("Cannot delete question with existing student answers.");
                    }

                    questionRepository.delete(existingQuestion); // Xóa Question khỏi database
                    isUpdated.set(true); // Đánh dấu khi có thay đổi
                }
                return toBeRemoved; // Xóa khỏi danh sách trong bộ nhớ
            });

            for (QuestionDTO questionDTO : courseDTO.getQuestions()) {
                Question existingQuestion;

                // Nếu `id` không tồn tại trong database, tạo mới thực thể với `id` từ frontend
                if (questionDTO.getId() != null) {
                    existingQuestion = existingCourse.getQuestions().stream()
                            .filter(q -> q.getId().equals(questionDTO.getId()))
                            .findFirst()
                            .orElse(null);

                    // Nếu không tìm thấy, tạo mới Question với `id` từ frontend
                    if (existingQuestion == null) {
                        existingQuestion = new Question();
                        existingQuestion.setId(questionDTO.getId()); // Gán `id` từ frontend
                        existingQuestion.setCourse(existingCourse); // Gán Course hiện tại
                        existingCourse.getQuestions().add(existingQuestion);
                        isUpdated.set(true);
                    }
                } else {
                    throw new IllegalArgumentException("ID of question is required from frontend.");
                }

                // Cập nhật thông tin của Question
                if (!Objects.equals(existingQuestion.getContent(), questionDTO.getContent())) {
                    existingQuestion.setContent(questionDTO.getContent());
                    isUpdated.set(true);
                }
                if (!Objects.equals(existingQuestion.getType(), questionDTO.getType())) {
                    existingQuestion.setType(questionDTO.getType());
                    isUpdated.set(true);
                }
                if (!Objects.equals(existingQuestion.getMarks(), questionDTO.getMarks())) {
                    existingQuestion.setMarks(questionDTO.getMarks());
                    isUpdated.set(true);
                }

                // Xử lý AnswerOptions của Question
                if (questionDTO.getAnswerOptions() != null) {
                    // Lấy danh sách ID của AnswerOptions từ DTO
                    List<Long> answerOptionIdsFromDTO = questionDTO.getAnswerOptions().stream()
                            .map(AnswerOptionDTO::getId)
                            .filter(Objects::nonNull)
                            .toList();

                    // Xóa các AnswerOptions không còn trong DTO
                    existingQuestion.getAnswerOptions().removeIf(existingOption -> {
                        boolean toBeRemoved = !answerOptionIdsFromDTO.contains(existingOption.getId());
                        if (toBeRemoved) {
                            // Kiểm tra nếu `answers` tồn tại
                            if (!existingOption.getAnswers().isEmpty()) {
                                throw new IllegalStateException("Cannot delete answer option with existing student answers.");
                            }

                            answerOptionRepository.delete(existingOption); // Xóa AnswerOption khỏi database
                            isUpdated.set(true); // Đánh dấu khi có thay đổi
                        }
                        return toBeRemoved;
                    });

                    // Cập nhật hoặc thêm mới AnswerOptions từ DTO
                    for (AnswerOptionDTO answerOptionDTO : questionDTO.getAnswerOptions()) {
                        AnswerOption existingOption = existingQuestion.getAnswerOptions().stream()
                                .filter(opt -> opt.getId().equals(answerOptionDTO.getId()))
                                .findFirst()
                                .orElse(null);

                        // Nếu không tồn tại, tạo mới với `id` từ frontend
                        if (existingOption == null) {
                            existingOption = new AnswerOption();
                            existingOption.setId(answerOptionDTO.getId()); // Gán `id` từ frontend
                            existingOption.setQuestion(existingQuestion);
                            existingQuestion.getAnswerOptions().add(existingOption);
                            isUpdated.set(true);
                        }

                        // Cập nhật thông tin của AnswerOption
                        if (!Objects.equals(existingOption.getContent(), answerOptionDTO.getContent())) {
                            existingOption.setContent(answerOptionDTO.getContent());
                            isUpdated.set(true);
                        }
                        if (!Objects.equals(existingOption.getIsCorrect(), answerOptionDTO.getIsCorrect())) {
                            existingOption.setIsCorrect(answerOptionDTO.getIsCorrect());
                            isUpdated.set(true);
                        }
                    }
                }
            }
        }

        if (isUpdated.get()) {
            existingCourse.setStatus(ECourseStatus.PENDING);
        }

        courseRepository.save(existingCourse);

        return courseMapper.courseToCourseDTO(existingCourse);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public CourseDetailDTO getCourseDetailsById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        CourseDetailDTO dto = courseMapper.courseToCourseDetailDTO(course);
        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
        }
        return dto;
    }

    public List<CourseDTO> getCoursesByInstructorId(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        List<Course> courses = courseRepository.findByInstructor(instructor);
        return courses.stream().map(course -> {
            // Chuyển đổi từ List<Course> sang List<CourseDTO>
            CourseDTO dto = courseMapper.courseToCourseDTO(course);
            if (course.getImageCover() != null) {
                dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
                if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                    dto.getInstructorInfo().setPhoto("http://localhost:8080/uploads/instructor/" + course.getInstructor().getPhoto());
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByInstructorIdIdAndStatus(Long instructorId, ECourseStatus status) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with instructor ID: " + instructorId));

        List<Course> courses = courseRepository.findByInstructorAndStatus(instructor, status);

        return courses.stream()
                .map(courseMapper::courseToCourseDTO)
                .collect(Collectors.toList());
    }

    public CourseDetailDTO getCourseDetailsForStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        CourseDetailDTO dto = courseMapper.courseToCourseDetailDTO(course);

        // Lấy tất cả bài giảng trong khóa học
        List<Lecture> allLectures = course.getSections().stream()
                .flatMap(section -> section.getLectures().stream())
                .collect(Collectors.toList());
        int totalLectures = allLectures.size();

        // Lấy danh sách các ID bài giảng đã hoàn thành
        List<Long> completedLectureIds = studentLectureProgressRepository.findCompletedLectureIdsByStudentAndCourse(studentId, courseId);

        // Map sections và bài giảng, đặt trạng thái 'completed'
        List<SectionDTO> sectionsDTO = course.getSections().stream().map(section -> {
            SectionDTO sectionDTO = new SectionDTO();
            sectionDTO.setId(section.getId());
            sectionDTO.setTitleSection(section.getTitleSection());
            sectionDTO.setLectures(section.getLectures().stream().map(lecture -> {
                LectureDTO lectureDTO = new LectureDTO();
                lectureDTO.setId(lecture.getId());
                lectureDTO.setTitleLecture(lecture.getTitleLecture());
                lectureDTO.setLectureVideo(lecture.getLectureVideo());
                lectureDTO.setLectureResource(lecture.getLectureResource());
                lectureDTO.setLectureOrder(lecture.getLectureOrder());
                lectureDTO.setArticles(lecture.getArticles().stream()
                        .map(article -> new ArticleDTO(article.getId(), article.getContent(), article.getFileUrl(), lecture.getId()))
                        .collect(Collectors.toList()));
                lectureDTO.setCompleted(completedLectureIds.contains(lecture.getId()));
                return lectureDTO;
            }).collect(Collectors.toList()));
            return sectionDTO;
        }).collect(Collectors.toList());

        dto.setSections(sectionsDTO);

        // Kiểm tra xem tất cả bài giảng đã hoàn thành chưa
        boolean allCompleted = completedLectureIds.size() == totalLectures && totalLectures > 0;
        dto.setAllLecturesCompleted(allCompleted);

        // Kiểm tra test cuối khóa
        Optional<Test> finalTestOpt = testRepository.findFinalTestByCourseId(courseId);
        if (finalTestOpt.isPresent()) {
            dto.setFinalTestId(finalTestOpt.get().getId());
            dto.setFinalTestTitle(finalTestOpt.get().getTitle());
        }

        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
        }
        return dto;
    }

    private Long getInstructorIdFromRequest(HttpServletRequest request) {
        String jwt = getJwtFromCookies(request);
        if (jwt == null || jwt.isEmpty()) {
//            System.out.println("JWT is missing in the request.");
            throw new IllegalArgumentException("JWT token is missing or empty.");
        }

//        System.out.println("JWT Token: " + jwt);
        return jwtUtil.getInstructorIdFromJwtToken(jwt);
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        return jwtUtil.getCookieValueByName(request, "token");
    }
}
