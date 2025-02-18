package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ECourseStatus;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.AccountMapper;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    public List<AccountDTO> getPendingInstructors() {
        List<Account> pendingInstructors = accountRepository.findAll().stream()
                .filter(a -> a.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("INSTRUCTOR")) && a.getStatus() == EAccountStatus.PENDING)
                .toList();

        return pendingInstructors.stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> approveInstructor(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        boolean isInstructor = account.getRoles().stream()
                .anyMatch(r -> r.getName().equals("INSTRUCTOR"));

        if (!isInstructor) {
            return ResponseEntity.badRequest().body(new MessageDTO("Account is not an instructor."));
        }

        // Kiểm tra trạng thái hiện tại của tài khoản
        if (account.getStatus() != EAccountStatus.PENDING) {
            return ResponseEntity.badRequest().body(new MessageDTO("Instructor account is not in 'PENDING' status."));
        }

        // Cập nhật trạng thái tài khoản thành ACTIVE
        account.setStatus(EAccountStatus.ACTIVE);
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("Instructor account approved successfully."));
    }

    // Phương thức từ chối giảng viên
    public ResponseEntity<?> rejectInstructor(Long accountId, String rejectReason) {
        // Tìm tài khoản theo ID
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Kiểm tra xem tài khoản này có phải giảng viên không
        boolean isInstructor = account.getRoles().stream()
                .anyMatch(r -> r.getName().equals("INSTRUCTOR"));

        if (!isInstructor) {
            return ResponseEntity.badRequest().body(new MessageDTO("Account is not an instructor."));
        }

        // Kiểm tra trạng thái hiện tại của tài khoản
        if (account.getStatus() != EAccountStatus.PENDING) {
            return ResponseEntity.badRequest().body(new MessageDTO("Instructor account is not in 'PENDING' status."));
        }

        // Cập nhật trạng thái tài khoản thành REJECTED
        account.setStatus(EAccountStatus.REJECTED);
        accountRepository.save(account);

        // Gửi thông báo lý do từ chối
        return ResponseEntity.ok(new MessageDTO("Instructor account rejected. Reason: " + rejectReason));
    }

    public ResponseEntity<?> updateAccountStatus(Long accountId, EAccountStatus newStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));


        account.setStatus(newStatus);
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("Account status updated successfully to " + newStatus));
    }

    public List<AccountDTO> getAccountsByStatus(EAccountStatus status) {
        List<Account> accounts = accountRepository.findAll().stream()
                .filter(a -> a.getStatus() == status)
                .toList();

        return accounts.stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    // Phương thức phê duyệt khóa học
    public ResponseEntity<?> approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (course.getStatus() != ECourseStatus.PENDING) {
            return ResponseEntity.badRequest().body(new MessageDTO("Course is not in 'PENDING' status."));
        }

        // Cập nhật trạng thái khóa học thành APPROVED
        course.setStatus(ECourseStatus.APPROVED);
        courseRepository.save(course);

        return ResponseEntity.ok(new MessageDTO("Course approved successfully."));
    }

    // Phương thức từ chối khóa học
    public ResponseEntity<?> rejectCourse(Long courseId, String rejectReason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (course.getStatus() != ECourseStatus.PENDING) {
            return ResponseEntity.badRequest().body(new MessageDTO("Course is not in 'PENDING' status."));
        }

        // Cập nhật trạng thái khóa học thành REJECTED
        course.setStatus(ECourseStatus.REJECTED);
        courseRepository.save(course);

        return ResponseEntity.ok(new MessageDTO("Course rejected. Reason: " + rejectReason));
    }
    public List<CourseDTO> getCoursesByStatus(ECourseStatus status) {
        List<Course> courses = courseRepository.findAll().stream()
                .filter(c -> c.getStatus() == status)
                .toList();

        return courses.stream()
                .map(courseMapper::courseToCourseDTO)
                .collect(Collectors.toList());
    }
}
