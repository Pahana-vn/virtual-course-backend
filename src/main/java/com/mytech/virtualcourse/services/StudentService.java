package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.DashboardDTO;
import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.LearningProgress;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.mappers.StudentMapper;
import com.mytech.virtualcourse.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private LearningProgressRepository learningProgressRepository;
    @Autowired
    private AccountRepository accountRepository; // Thêm repository của Account

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

//    public StudentDTO createStudent(StudentDTO studentDTO) {
//        Student student = studentMapper.studentDTOToStudent(studentDTO);
//        Student savedStudent = studentRepository.save(student);
//        return studentMapper.studentToStudentDTO(savedStudent);
//    }
public StudentDTO createStudent(Long accountId, StudentDTO studentDTO) {
    // Kiểm tra xem accountId có được cung cấp không
    if (studentDTO.getAccountId() == null) {
        throw new IllegalArgumentException("Account ID cannot be null");
    }

    // Lấy Account từ accountId
    Account account = accountRepository.findById(studentDTO.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + studentDTO.getAccountId()));

    // Kiểm tra xem Account đã có Student chưa
    if (account.getStudent() != null) {
        throw new RuntimeException("Account already has a Student.");
    }

    // Map DTO sang Student entity
    Student student = studentMapper.studentDTOToStudent(studentDTO);
    student.setAccount(account); // Thiết lập Account cho Student

    // Liên kết Student với Account
    account.setStudent(student);

    // Lưu Student vào cơ sở dữ liệu
    Student savedStudent = studentRepository.save(student);
    return studentMapper.studentToStudentDTO(savedStudent);
}

    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Nếu avatar cũ khác avatar mới => xóa avatar cũ
        if (existingStudent.getAvatar() != null
                && !existingStudent.getAvatar().isEmpty()
                && studentDTO.getAvatar() != null
                && !studentDTO.getAvatar().equals(existingStudent.getAvatar())) {

            // Xóa file cũ trong uploads/student
            fileStorageService.deleteFile(existingStudent.getAvatar(), "student");
        }

        // Cập nhật các trường
        existingStudent.setFirstName(studentDTO.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName());
        existingStudent.setDob(studentDTO.getDob());
        existingStudent.setAddress(studentDTO.getAddress());
        existingStudent.setGender(studentDTO.getGender());
        existingStudent.setPhone(studentDTO.getPhone());
//        existingStudent.setBio(studentDTO.getBio());
        existingStudent.setCategoryPrefer(studentDTO.getCategoryPrefer());
        existingStudent.setStatusStudent(studentDTO.getStatusStudent());
        existingStudent.setVerifiedPhone(studentDTO.isVerifiedPhone());

        // Cập nhật avatar (nếu có)
        if (studentDTO.getAvatar() != null) {
            existingStudent.setAvatar(studentDTO.getAvatar());
        }

        // Lưu vào cơ sở dữ liệu
        Student updatedStudent = studentRepository.save(existingStudent);
        logger.info("Student updated successfully with id: {}", updatedStudent.getId());

        return studentMapper.studentToStudentDTO(updatedStudent);
    }
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public DashboardDTO getStudentDashboardData(Long accountId) {

        Student student = studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with account id: " + accountId));
        Long studentId = student.getId();


        List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentId(studentId);
        List<Course> enrolledCourses = learningProgresses.stream()
                .map(LearningProgress::getCourse)
                .distinct()
                .toList();


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

    public Map<String, List<CourseDTO>> getStudentCourses(Long accountId) {

        Student student = studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with account ID: " + accountId));


        List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentId(student.getId());

        // Categorize courses
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


        Map<String, List<CourseDTO>> categorizedCourses = new HashMap<>();
        categorizedCourses.put("enrolled", mapCoursesWithFullImageUrl(enrolledCourses));
        categorizedCourses.put("active", mapCoursesWithFullImageUrl(activeCourses));
        categorizedCourses.put("completed", mapCoursesWithFullImageUrl(completedCourses));

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
}
