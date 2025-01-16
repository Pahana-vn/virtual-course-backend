package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.CourseLevel;
import com.mytech.virtualcourse.enums.EStatusCourse;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.*;
import com.mytech.virtualcourse.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
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
    private TestRepository testRepository;

    @Autowired
    private StudentLectureProgressRepository studentLectureProgressRepository;

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
        CourseDTO dto = courseMapper.courseToCourseDTO(course);
        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/courses/" + course.getImageCover());
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
//        course.setCreatedAt(LocalDateTime.now());
//        course.setUpdatedAt(LocalDateTime.now());

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

    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        existingCourse.setTitleCourse(courseDTO.getTitleCourse());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setBasePrice(courseDTO.getBasePrice());
        existingCourse.setDuration(courseDTO.getDuration());
        existingCourse.setImageCover(courseDTO.getImageCover());
        existingCourse.setUrlVideo(courseDTO.getUrlVideo());
        // Cập nhật danh sách hashtags nếu có
//        if (courseDTO.getHashtags() != null) {
//            existingCourse.setHashtags(courseDTO.getHashtags());
//        }

        // Cập nhật level (enum)
        if (courseDTO.getLevel() != null) {
            try {
                CourseLevel level = CourseLevel.valueOf(courseDTO.getLevel().toUpperCase());
                existingCourse.setLevel(level);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid level value: " + courseDTO.getLevel());
            }
        }

        // Cập nhật status (enum)
        if (courseDTO.getStatus() != null) {
            try {
                EStatusCourse status = EStatusCourse.valueOf(courseDTO.getStatus().toUpperCase());
                existingCourse.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + courseDTO.getStatus());
            }
        }

        Course updatedCourse = courseRepository.save(existingCourse);
        return courseMapper.courseToCourseDTO(updatedCourse);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public List<CourseDTO> mapCoursesWithFullImageUrl(List<Course> courses) {
        return courses.stream()
                .map(course -> {
                    CourseDTO dto = courseMapper.courseToCourseDTO(course);
                    if (course.getImageCover() != null) {
                        dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
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

    public CourseDetailDTO getCourseDetailsForStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        CourseDetailDTO dto = courseMapper.courseToCourseDetailDTO(course);

        // Lấy tất cả bài giảng trong khóa học
        List<Lecture> allLectures = course.getSections().stream()
                .flatMap(section -> section.getLectures().stream())
                .toList();
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
                        .map(article -> new ArticleDTO(article.getId(), article.getContent(), article.getFileUrl()))
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
}
