package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CartItemDTO;
import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.DashboardDTO;
import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.mappers.StudentMapper;
import com.mytech.virtualcourse.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CourseMapper courseMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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

        existingStudent.setFirstName(studentDTO.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName());
        existingStudent.setDob(studentDTO.getDob());
        existingStudent.setAddress(studentDTO.getAddress());
        existingStudent.setPhone(studentDTO.getPhone());
        existingStudent.setAvatar(studentDTO.getAvatar());
        existingStudent.setVerifiedPhone(studentDTO.getVerifiedPhone());
        existingStudent.setCategoryPrefer(studentDTO.getCategoryPrefer());
        existingStudent.setStatusStudent(studentDTO.getStatusStudent());

        Student updatedStudent = studentRepository.save(existingStudent);
        StudentDTO dto = studentMapper.studentToStudentDTO(updatedStudent);
        if (updatedStudent.getAvatar() != null) {
            dto.setAvatar(AVATAR_BASE_URL + updatedStudent.getAvatar());
        }
        return dto;
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

    public Map<String, List<CourseDTO>> getStudentCourses(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        // Lấy tất cả LearningProgress cho student
        List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentId(student.getId());

        // Tách ra các nhóm khóa học (enrolled, active, completed) dựa vào LearningProgress
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

        // Hàm tiện ích để map Course -> CourseDTO kèm progress
        // Hàm này sẽ tìm LearningProgress tương ứng (studentId, courseId) để lấy progress.
        // Nếu không có thì progress = 0.
        Function<Course, CourseDTO> toCourseDTOWithProgress = (course) -> {
            CourseDTO dto = courseMapper.courseToCourseDTO(course);
            Optional<LearningProgress> lpOpt = learningProgressRepository.findByStudentIdAndCourseId(studentId, course.getId());
            int progress = lpOpt.map(LearningProgress::getProgressPercentage).orElse(0);
            dto.setProgress(progress);

            // Nếu cần set lại imageCover đầy đủ URL (do mapper đã set sẵn nhưng có thể cần override)
            if (course.getImageCover() != null) {
                dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
            }

            return dto;
        };

        // Map enrolledCourses sang DTO kèm theo progress
        List<CourseDTO> enrolledDTO = enrolledCourses.stream()
                .map(toCourseDTOWithProgress)
                .collect(Collectors.toList());

        // Map activeCourses sang DTO kèm theo progress
        List<CourseDTO> activeDTO = activeCourses.stream()
                .map(toCourseDTOWithProgress)
                .collect(Collectors.toList());

        // Map completedCourses sang DTO kèm theo progress
        // Đối với completed, nếu lp không tìm thấy, đặt mặc định 100%
        List<CourseDTO> completedDTO = completedCourses.stream()
                .map(course -> {
                    CourseDTO dto = courseMapper.courseToCourseDTO(course);
                    Optional<LearningProgress> lpOpt = learningProgressRepository.findByStudentIdAndCourseId(studentId, course.getId());
                    int progress = lpOpt.map(LearningProgress::getProgressPercentage).orElse(100);
                    dto.setProgress(progress);

                    if (course.getImageCover() != null) {
                        dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        // Cuối cùng tạo Map chứa 3 danh sách
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


    public List<CourseDTO> getWishlist(Long studentId) {
        // Tìm sinh viên
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Lấy danh sách khóa học từ wishlist của sinh viên
        List<FavoriteCourse> wishlistItems = favoriteCourseRepository.findByStudent(student);

        // Chuyển đổi danh sách FavoriteCourse thành danh sách CourseDTO
        return wishlistItems.stream()
                .map(fav -> {
                    Course course = fav.getCourse();
                    CourseDTO courseDTO = courseMapper.courseToCourseDTO(course);
                    if (course.getImageCover() != null) {
                        courseDTO.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
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
        // Kiểm tra xem sinh viên có tồn tại không
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Kiểm tra xem cartItem có tồn tại không
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Kiểm tra xem CartItem có thuộc về Cart của sinh viên này không
        if (!cartItem.getCart().getStudent().equals(student)) {
            throw new ResourceNotFoundException("Cart item does not belong to the student");
        }

        // Xóa item khỏi giỏ hàng
        cartItemRepository.delete(cartItem);
    }

    public Map<String, List<CourseDTO>> getStudentPurchasedCourses(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        // Lấy tất cả khóa học đã mua từ ManyToMany
        List<Course> purchasedCourses = student.getCourses();

        // Map sang DTO
        List<CourseDTO> purchasedDTOs = mapCoursesWithFullImageUrl(purchasedCourses);

        // Ở đây bạn có thể phân loại tùy thích. Nếu không cần phân loại nữa,
        // bạn có thể đặt tất cả vào "enrolled"
        Map<String, List<CourseDTO>> categorizedCourses = new HashMap<>();
        categorizedCourses.put("enrolled", purchasedDTOs);
        categorizedCourses.put("active", new ArrayList<>());
        categorizedCourses.put("completed", new ArrayList<>());
        return categorizedCourses;
    }

    public void enrollStudentToCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Thêm khóa học vào student (nếu chưa)
        if (!student.getCourses().contains(course)) {
            student.getCourses().add(course);
            studentRepository.save(student); // Lưu để cập nhật student_course mapping
        }

        // Kiểm tra nếu LearningProgress đã tồn tại
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

    public String getStudentAvatar(Long id) {
        if (!studentRepository.existsStudentByAccountId(id)) {
            throw new ResourceNotFoundException("Student not found with account id: " + id);
        }
        Optional<Student> optionalStudent = studentRepository.findByAccountId(id);

        if (optionalStudent.isPresent()) {
            return optionalStudent.get().getAvatar();
        } else {
            throw new ResourceNotFoundException("Student not found with account id: " + id);
        }
    }
}
