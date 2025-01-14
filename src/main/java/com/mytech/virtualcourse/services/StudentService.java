package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.DashboardDTO;
import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.StatusWallet;
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
import java.sql.Timestamp;
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
    private WalletRepository walletRepository; // Thêm WalletRepository

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
    private AdminAccountRepository accountRepository; // Thêm repository của Account

    // Lưu ý: DB sẽ chỉ chứa filename. Còn prefix dùng để hiển thị
    private static final String AVATAR_BASE_URL = "http://localhost:8080/uploads/student/";
    private static final String INSTRUCTOR_PHOTO_BASE_URL = "http://localhost:8080/uploads/instructor/";
    private static final String COURSE_IMAGE_BASE_URL = "http://localhost:8080/uploads/course/";

    // ========================= GET ALL =========================
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream().map(student -> {
            StudentDTO dto = studentMapper.studentToStudentDTO(student);
            // Nếu DB đang có student.avatar = "myphoto.png"
            // => GHÉP prefix cho dto trả về client
            if (student.getAvatar() != null && !student.getAvatar().isEmpty()) {
                dto.setAvatar(AVATAR_BASE_URL + student.getAvatar());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // ========================= GET BY ID =========================
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        StudentDTO dto = studentMapper.studentToStudentDTO(student);
        if (student.getAvatar() != null && !student.getAvatar().isEmpty()) {
            dto.setAvatar(AVATAR_BASE_URL + student.getAvatar());
        }
        return dto;
    }

    public StudentDTO getStudentByAccountId(Long accountId) {
        Student student = studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with account id: " + accountId));

        StudentDTO dto = studentMapper.studentToStudentDTO(student);
        if (student.getAvatar() != null && !student.getAvatar().isEmpty()) {
            dto.setAvatar(AVATAR_BASE_URL + student.getAvatar());
        }
        return dto;
    }

    // ========================= CREATE (ADD) =========================
    public StudentDTO createStudent(Long accountId, StudentDTO studentDTO) {
        if (studentDTO.getAccountId() == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }

        Account account = accountRepository.findById(studentDTO.getAccountId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Account not found with id: " + studentDTO.getAccountId()));

        // Check account đã có student chưa?
        if (account.getStudent() != null) {
            throw new RuntimeException("Account already has a Student.");
        }

        // Map DTO sang entity. Lưu ý studentDTO.avatar = "filename.png" (chứ ko phải
        // URL)
        Student student = studentMapper.studentDTOToStudent(studentDTO);
        student.setAccount(account); // gán account
        account.setStudent(student); // link 2 chiều

        // Tạo Wallet cho Student
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatusWallet(StatusWallet.ACTIVE);
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        wallet.setStudent(student); // Liên kết với Student

        student.setWallet(wallet); // Liên kết Student với Wallet

        Student saved = studentRepository.save(student); // CascadeType.ALL sẽ lưu cả Wallet

        // Convert => DTO => GHÉP prefix khi trả về
        StudentDTO dto = studentMapper.studentToStudentDTO(saved);
        if (saved.getAvatar() != null && !saved.getAvatar().isEmpty()) {
            dto.setAvatar(AVATAR_BASE_URL + saved.getAvatar());
        }
        dto.setWalletId(wallet.getId()); // Đảm bảo DTO có trường walletId
        return dto;
    }

    // ========================= UPDATE =========================
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        // Tìm student cũ
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        String oldAvatar = existingStudent.getAvatar(); // e.g. "oldPhoto.png"
        String newAvatar = studentDTO.getAvatar(); // e.g. "newPhoto.png" (chỉ filename, ko URL)

        // Nếu cũ != null && mới != null && cũ khác mới => xóa file cũ
        if (oldAvatar != null && !oldAvatar.isEmpty()
                && newAvatar != null && !newAvatar.equals(oldAvatar)) {
            fileStorageService.deleteFile(oldAvatar, "student");
        }

        // Cập nhật các trường khác
        existingStudent.setFirstName(studentDTO.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName());
        existingStudent.setDob(studentDTO.getDob());
        existingStudent.setAddress(studentDTO.getAddress());
        existingStudent.setGender(studentDTO.getGender());
        existingStudent.setPhone(studentDTO.getPhone());
        // existingStudent.setBio(studentDTO.getBio()); // nếu có cột bio
        existingStudent.setCategoryPrefer(studentDTO.getCategoryPrefer());
//        existingStudent.setStatusStudent(studentDTO.getStatusStudent());
        existingStudent.setVerifiedPhone(studentDTO.isVerifiedPhone());

        // Cập nhật avatar
        if (newAvatar != null) {
            existingStudent.setAvatar(newAvatar);
        }

        Student updated = studentRepository.save(existingStudent);
        logger.info("Student updated successfully with id: {}", updated.getId());

        // Convert => DTO => GHÉP prefix
        StudentDTO dto = studentMapper.studentToStudentDTO(updated);
        if (updated.getAvatar() != null && !updated.getAvatar().isEmpty()) {
            dto.setAvatar(AVATAR_BASE_URL + updated.getAvatar());
        }
        return dto;
    }

    // ========================= DELETE =========================
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // ========================= DISABLE / ENABLE =========================
//    public void disableStudent(Long studentId) {
//        Student student = findStudentById(studentId);
//        student.setStatusStudent("INACTIVE");
//        studentRepository.save(student);
//    }
//
//    public void enableStudent(Long studentId) {
//        Student student = findStudentById(studentId);
//        student.setStatusStudent("ACTIVE");
//        studentRepository.save(student);
//    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    // ========================= DASHBOARD, ETC. (giữ nguyên)
    // =========================
    public DashboardDTO getStudentDashboardData(Long accountId) {
        // ... (giữ nguyên phần code fetch dashboard)
        // ...
        // return ...
        return null; // demo
    }

    public Map<String, List<CourseDTO>> getStudentCourses(Long accountId) {
        // ... (giữ nguyên)
        return new HashMap<>();
    }
}
