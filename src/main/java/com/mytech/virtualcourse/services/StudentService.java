package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.enums.QuestionType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.mappers.StudentMapper;
import com.mytech.virtualcourse.mappers.StudentQuizMapper;
import com.mytech.virtualcourse.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentQuizMapper studentQuizMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentTestSubmissionRepository submissionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private LearningProgressRepository learningProgressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FavoriteCourseRepository favoriteCourseRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final String AVATAR_BASE_URL = "http://localhost:8080/uploads/student/";
    private static final String INSTRUCTOR_PHOTO_BASE_URL = "http://localhost:8080/uploads/instructor/";
    private static final String COURSE_IMAGE_BASE_URL = "http://localhost:8080/uploads/course/";

    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(student -> {
                    StudentDTO dto = studentMapper.studentToStudentDTO(student);
                    if (student.getAvatar() != null) {
                        dto.setAvatar(AVATAR_BASE_URL + student.getAvatar());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        StudentDTO dto = studentMapper.studentToStudentDTO(student);
        if (student.getAvatar() != null) {
            dto.setAvatar(AVATAR_BASE_URL + student.getAvatar());
        }
        return dto;
    }

    public StudentDTO getStudentByAccountId(Long accountId) {
        Student student = studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with account id: " + accountId));
        StudentDTO dto = studentMapper.studentToStudentDTO(student);
        if (student.getAvatar() != null) {
            dto.setAvatar(AVATAR_BASE_URL + student.getAvatar());
        }
        return dto;
    }

    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = studentMapper.studentDTOToStudent(studentDTO);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.studentToStudentDTO(savedStudent);
    }

    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        existingStudent.setFirstName(studentDTO.getFirstName() != null ? studentDTO.getFirstName() : existingStudent.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName() != null ? studentDTO.getLastName() : existingStudent.getLastName());
        existingStudent.setDob(studentDTO.getDob() != null ? studentDTO.getDob() : existingStudent.getDob());
        existingStudent.setAddress(studentDTO.getAddress() != null ? studentDTO.getAddress() : existingStudent.getAddress());
        existingStudent.setGender(studentDTO.getGender() != null ? Gender.valueOf(studentDTO.getGender()) : existingStudent.getGender());
        existingStudent.setPhone(studentDTO.getPhone() != null ? studentDTO.getPhone() : existingStudent.getPhone());
        existingStudent.setBio(studentDTO.getBio() != null ? studentDTO.getBio() : existingStudent.getBio());

        if (studentDTO.getUsername() != null) {
            Account account = existingStudent.getAccount();
            if (account != null) {
                account.setUsername(studentDTO.getUsername());
                accountRepository.save(account);
            }
        }

        if (studentDTO.getAvatar() != null && !studentDTO.getAvatar().isEmpty()) {
            String avatarFileName = studentDTO.getAvatar().replace(AVATAR_BASE_URL, "");
            existingStudent.setAvatar(avatarFileName);
        }



        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.studentToStudentDTO(updatedStudent);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public DashboardDTO getStudentDashboardData(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentId(studentId);
        List<Course> enrolledCourses = learningProgresses.stream()
                .map(LearningProgress::getCourse)
                .distinct()
                .collect(Collectors.toList());


        int totalCourses = enrolledCourses.size();


        int completedCourses = (int) learningProgresses.stream()
                .filter(LearningProgress::getCompleted)
                .map(LearningProgress::getCourse)
                .distinct()
                .count();


        BigDecimal totalPaid = enrolledCourses.stream()
                .map(Course::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        List<CourseDTO> recentCoursesDTO = enrolledCourses.stream()
                .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                .limit(5)
                .map(course -> {
                    CourseDTO dto = courseMapper.courseToCourseDTO(course);
                    if (course.getImageCover() != null) {
                        dto.setImageCover(COURSE_IMAGE_BASE_URL + course.getImageCover());
                    }
                    if (course.getInstructor() != null) {
                        dto.setInstructorFirstName(course.getInstructor().getFirstName());
                        dto.setInstructorLastName(course.getInstructor().getLastName());
                        dto.setInstructorPhoto(course.getInstructor().getPhoto() != null
                                ? INSTRUCTOR_PHOTO_BASE_URL + course.getInstructor().getPhoto()
                                : null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());


        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setTotalCourses(totalCourses);
        dashboard.setCompletedCourses(completedCourses);
        dashboard.setTotalPaid(totalPaid);
        dashboard.setRecentCourses(recentCoursesDTO);

        return dashboard;
    }

    public Map<String, List<CourseDTO>> getStudentCourses(Long studentId, String platform) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentId(student.getId());

        List<Course> enrolledCourses = learningProgresses.stream()
                .map(LearningProgress::getCourse)
                .distinct()
                .collect(Collectors.toList());

        List<Course> activeCourses = learningProgresses.stream()
                .filter(lp -> lp.getProgressPercentage() < 100 && !lp.getCompleted())
                .map(LearningProgress::getCourse)
                .distinct()
                .collect(Collectors.toList());

        List<Course> completedCourses = learningProgresses.stream()
                .filter(LearningProgress::getCompleted)
                .map(LearningProgress::getCourse)
                .distinct()
                .collect(Collectors.toList());

        String baseUrl = (platform != null && platform.equals("flutter"))
                ? "http://10.0.2.2:8080"
                : "http://localhost:8080";

        Function<Course, CourseDTO> toCourseDTOWithProgress = (course) -> {
            CourseDTO dto = courseMapper.courseToCourseDTO(course);
            Optional<LearningProgress> lpOpt = learningProgressRepository.findByStudentIdAndCourseId(studentId, course.getId());
            int progress = lpOpt.map(LearningProgress::getProgressPercentage).orElse(0);
            dto.setProgress(progress);

            if (course.getImageCover() != null) {
                dto.setImageCover(baseUrl + "/uploads/course/" + course.getImageCover());
            }
            if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                dto.setInstructorPhoto(baseUrl + "/uploads/instructor/" + course.getInstructor().getPhoto());
            }

            return dto;
        };

        List<CourseDTO> enrolledDTO = enrolledCourses.stream()
                .map(toCourseDTOWithProgress)
                .collect(Collectors.toList());

        List<CourseDTO> activeDTO = activeCourses.stream()
                .map(toCourseDTOWithProgress)
                .collect(Collectors.toList());

        List<CourseDTO> completedDTO = completedCourses.stream()
                .map(course -> {
                    CourseDTO dto = courseMapper.courseToCourseDTO(course);
                    Optional<LearningProgress> lpOpt = learningProgressRepository.findByStudentIdAndCourseId(studentId, course.getId());
                    int progress = lpOpt.map(LearningProgress::getProgressPercentage).orElse(100);
                    dto.setProgress(progress);

                    if (course.getImageCover() != null) {
                        dto.setImageCover(baseUrl + "/uploads/course/" + course.getImageCover());
                    }
                    if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                        dto.setInstructorPhoto(baseUrl + "/uploads/instructor/" + course.getInstructor().getPhoto());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        Map<String, List<CourseDTO>> categorizedCourses = new HashMap<>();
        categorizedCourses.put("enrolled", enrolledDTO);
        categorizedCourses.put("active", activeDTO);
        categorizedCourses.put("completed", completedDTO);

        return categorizedCourses;
    }

    private List<CourseDTO> mapCoursesWithFullImageUrl(List<Course> courses) {
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

    public void addToWishlist(Long studentId, CourseDTO courseDTO) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseDTO.getId()));

        if (wishlistRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalArgumentException("This course is already in the wishlist.");
        }

        FavoriteCourse wishlist = new FavoriteCourse();
        wishlist.setStudent(student);
        wishlist.setCourse(course);
        wishlistRepository.save(wishlist);
    }


    public List<CourseDTO> getWishlist(Long studentId, String platform) {
        // Tìm sinh viên
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<FavoriteCourse> wishlistItems = favoriteCourseRepository.findByStudent(student);

        String baseUrl = (platform != null && platform.equals("flutter"))
                ? "http://10.0.2.2:8080"
                : "http://localhost:8080";

        return wishlistItems.stream()
                .map(fav -> {
                    Course course = fav.getCourse();
                    CourseDTO courseDTO = courseMapper.courseToCourseDTO(course);

                    if (course.getImageCover() != null) {
                        courseDTO.setImageCover(baseUrl + "/uploads/course/" + course.getImageCover());
                    }

                    if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                        courseDTO.setInstructorPhoto(baseUrl + "/uploads/instructor/" + course.getInstructor().getPhoto());
                    }

                    return courseDTO;
                })
                .collect(Collectors.toList());
    }

    public void addToCart(Long studentId, CourseDTO courseDTO) {
        System.out.println("Adding course to cart for student ID: " + studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        System.out.println("Student found: " + student.getFirstName());

        Course course = courseRepository.findById(courseDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseDTO.getId()));

        Cart cart = student.getCart();
        if (cart == null) {
            System.out.println("Creating new cart for student: " + studentId);
            cart = new Cart();
            cart.setStudent(student);
            cart = cartRepository.save(cart);
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndCourse(cart, course);
        if (existingCartItem.isPresent()) {
            throw new IllegalArgumentException("This course is already in the cart.");
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setCourse(course);
            cartItem.setQuantity(1);
            cartItemRepository.save(cartItem);
            System.out.println("Course added to cart successfully for student ID: " + studentId);
        }
    }


    public List<CartItemDTO> getCartItemsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("❌ Student not found with id: " + studentId));

        Cart cart = student.getCart();
        if (cart == null) {
            System.out.println("🛠 Tạo giỏ hàng mới cho studentId: " + studentId);
            cart = new Cart();
            cart.setStudent(student);
            cart = cartRepository.save(cart);
        }

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        return cartItems.stream()
                .map(cartItem -> new CartItemDTO(cartItem.getId(), courseMapper.courseToCourseDTO(cartItem.getCourse()), cartItem.getQuantity()))
                .collect(Collectors.toList());
    }


    public void removeFromCart(Long studentId, Long cartItemId) throws ResourceNotFoundException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getStudent().equals(student)) {
            throw new ResourceNotFoundException("Cart item does not belong to the student");
        }

        cartItemRepository.delete(cartItem);
    }

//    public Map<String, List<CourseDTO>> getStudentPurchasedCourses(Long studentId) {
//        Student student = studentRepository.findById(studentId)
//                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
//
//        List<Course> purchasedCourses = student.getCourses();
//
//        List<CourseDTO> purchasedDTOs = mapCoursesWithFullImageUrl(purchasedCourses);
//
//        Map<String, List<CourseDTO>> categorizedCourses = new HashMap<>();
//        categorizedCourses.put("enrolled", purchasedDTOs);
//        categorizedCourses.put("active", new ArrayList<>());
//        categorizedCourses.put("completed", new ArrayList<>());
//        return categorizedCourses;
//    }

    public void enrollStudentToCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (!student.getCourses().contains(course)) {
            student.getCourses().add(course);
            studentRepository.save(student);
        }

        Optional<LearningProgress> lpOpt = learningProgressRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (lpOpt.isEmpty()) {
            LearningProgress lp = new LearningProgress();
            lp.setStudent(student);
            lp.setCourse(course);
            lp.setProgressPercentage(0);
            lp.setCompleted(false);
            learningProgressRepository.save(lp);
        }
    }

    public void removeFromWishlist(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        FavoriteCourse favoriteCourse = favoriteCourseRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist entry not found for student and course"));

        favoriteCourseRepository.delete(favoriteCourse);
    }

    public List<StudentQuizResultDTO> getStudentQuizResults(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<StudentTestSubmission> submissions = submissionRepository.findByStudentId(studentId);

        return studentQuizMapper.toQuizResultDTOList(submissions);
    }

    public StudentQuizDetailDTO getQuizDetails(Long quizId) {
        StudentTestSubmission submission = submissionRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<Question> questions = questionRepository.findByTestId(submission.getTest().getId());

        List<StudentQuestionDTO> questionDTOs = questions.stream().map(question -> {
            StudentQuestionDTO studentQuestion = new StudentQuestionDTO();
            studentQuestion.setId(question.getId());
            studentQuestion.setContent(question.getContent());
            studentQuestion.setType(question.getType());
            studentQuestion.setMarks(question.getMarks());

            List<StudentAnswer> studentAnswers = submission.getAnswers().stream()
                    .filter(a -> a.getQuestion().getId().equals(question.getId()))
                    .toList();

            List<Long> selectedOptionIds = studentAnswers.stream()
                    .map(a -> a.getSelectedOption().getId())
                    .distinct()
                    .toList();

            List<AnswerOption> correctOptions = question.getAnswerOptions().stream()
                    .filter(AnswerOption::getIsCorrect)
                    .toList();
            List<Long> correctOptionIds = correctOptions.stream()
                    .map(AnswerOption::getId)
                    .toList();

            boolean isCorrect;
            if (question.getType() == QuestionType.MULTIPLE) {
                isCorrect = selectedOptionIds.size() == correctOptionIds.size()
                        && selectedOptionIds.containsAll(correctOptionIds);
            } else {
                isCorrect = selectedOptionIds.equals(correctOptionIds);
            }

            studentQuestion.setGivenAnswers(selectedOptionIds.stream()
                    .map(id -> {
                        AnswerOption opt = answerOptionRepository.findById(id).orElse(null);
                        return opt != null ? new AnswerOptionDTO(opt.getId(), opt.getContent(), opt.getIsCorrect(), question.getId()) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            studentQuestion.setCorrectAnswers(correctOptions.stream()
                    .map(opt -> new AnswerOptionDTO(opt.getId(), opt.getContent(), opt.getIsCorrect(), question.getId()))
                    .collect(Collectors.toList()));

            studentQuestion.setCorrect(isCorrect);

            return studentQuestion;
        }).collect(Collectors.toList());

        return new StudentQuizDetailDTO(
                submission.getId(),
                submission.getTest().getTitle(),
                submission.getTest().getTotalMarks(),
                submission.getMarksObtained(),
                (submission.getMarksObtained() * 100.0) / submission.getTest().getTotalMarks(),
                submission.getPassed(),
                questionDTOs
        );
    }

    public void changePassword(Long studentId, ChangePasswordStudentDTO changePasswordDTO) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Account account = student.getAccount();
        if (account == null) {
            throw new IllegalArgumentException("Student does not have an associated account");
        }

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        account.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        accountRepository.save(account);
    }
}
