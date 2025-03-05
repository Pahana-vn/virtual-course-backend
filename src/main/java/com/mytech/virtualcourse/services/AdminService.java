package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ECourseStatus;
import com.mytech.virtualcourse.enums.NotificationType;
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

    @Autowired
    private InstructorApprovalService instructorApprovalService;

    @Autowired
    private NotificationService notificationService;

    public List<AccountDTO> getPendingInstructors() {
        List<Account> pendingInstructors = accountRepository.findAll().stream()
                .filter(a -> a.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("INSTRUCTOR")) && a.getStatus() == EAccountStatus.PENDING)
                .toList();
        return pendingInstructors.stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> approveInstructor(Long accountId, String notes) {
        try {
            Account adminAccount = getAdminAccount("admin"); // Mặc định sử dụng admin account

            // Gọi service phê duyệt instructor
            ResponseEntity<?> response = instructorApprovalService.approveInstructor(accountId, adminAccount, notes);

            // Gửi thông báo cho instructor
            try {
                Account instructorAccount = accountRepository.findById(accountId)
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

                notificationService.sendNotification(
                        accountId,
                        "Your instructor application has been approved! You can now create courses.",
                        NotificationType.InstApprv, // Sử dụng enum NotificationType.InstApprv
                        null,
                        null
                );
                System.out.println("Sent notification to instructor with ID: " + accountId);
            } catch (Exception e) {
                System.err.println("Error sending notification: " + e.getMessage());
                // Không throw exception ở đây để không ảnh hưởng đến quá trình approve
            }

            return response;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Error approving instructor: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> rejectInstructor(Long accountId, String rejectReason) {
        try {
            Account adminAccount = getAdminAccount("admin"); // Mặc định sử dụng admin account

            // Gọi service từ chối instructor
            ResponseEntity<?> response = instructorApprovalService.rejectInstructor(accountId, adminAccount, rejectReason);

            // Gửi thông báo cho instructor
            try {
                Account instructorAccount = accountRepository.findById(accountId)
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

                notificationService.sendNotification(
                        accountId,
                        "Your instructor application was rejected. Reason: " + rejectReason,
                        NotificationType.InstRejct, // Sử dụng enum NotificationType.InstRejct
                        null,
                        null
                );
                System.out.println("Sent rejection notification to instructor with ID: " + accountId);
            } catch (Exception e) {
                System.err.println("Error sending rejection notification: " + e.getMessage());
                // Không throw exception ở đây để không ảnh hưởng đến quá trình reject
            }

            return response;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Error rejecting instructor: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> updateAccountStatus(Long accountId, EAccountStatus newStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        account.setStatus(newStatus);
        accountRepository.save(account);

        // Gửi thông báo khi cập nhật trạng thái tài khoản
        try {
            notificationService.sendNotification(
                    accountId,
                    "Your account status has been updated to " + newStatus,
                    NotificationType.AccStatus,
                    null,
                    null
            );
        } catch (Exception e) {
            System.err.println("Error sending account status notification: " + e.getMessage());
        }

        return ResponseEntity.ok(new MessageDTO("Account status updated successfully to " + newStatus));
    }

    public List<AccountDTO> getAccountsByStatus(EAccountStatus status) {
        try {
            List<Account> accounts = accountRepository.findByStatus(status);
            return accounts.stream()
                    .map(accountMapper::toAccountDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching accounts by status: " + e.getMessage());
        }
    }

    public List<CourseDTO> getPendingCourses() {
        return getCoursesByStatus(ECourseStatus.PENDING);
    }

    public List<CourseDTO> getCoursesByStatus(ECourseStatus status) {
        System.out.println("Fetching courses with status: " + status);
        List<Course> courses = courseRepository.findByStatus(status);
        System.out.println("Found " + courses.size() + " courses");
        return courses.stream()
                .map(courseMapper::courseToCourseDTO)
                .collect(Collectors.toList());
    }

    public Account getAdminAccount(String username) {
        System.out.println("Looking for admin account with username: " + username);
        // Nếu username là "user_1" (giá trị không hợp lệ), thử tìm tài khoản admin mặc định
        if ("user_1".equals(username)) {
            System.out.println("Detected test username 'user_1', trying to find default admin account");
            Account adminAccount = accountRepository.findByUsername("admin");
            if (adminAccount != null) {
                System.out.println("Using default admin account instead");
                return adminAccount;
            }
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            System.err.println("No account found with username: " + username);
            throw new ResourceNotFoundException("Admin account not found");
        }

        // Check if the account has admin role
        boolean isAdmin = account.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        if (!isAdmin) {
            System.err.println("Account found but does not have ADMIN role: " + username);
            throw new ResourceNotFoundException("Account is not an admin account");
        }

        System.out.println("Admin account found: " + account.getId() + " - " + account.getUsername());
        return account;
    }

    public List<String> getAccountRoles(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return account.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(Collectors.toList());
    }
}