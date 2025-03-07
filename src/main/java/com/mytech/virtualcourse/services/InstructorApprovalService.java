package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class InstructorApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(InstructorApprovalService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    /**
     * Phê duyệt tài khoản instructor
     * @param accountId ID của tài khoản cần phê duyệt
     * @param reviewerAccount Tài khoản admin thực hiện phê duyệt
     * @param notes Ghi chú khi phê duyệt (có thể null)
     * @return ResponseEntity chứa thông báo kết quả
     */
    public ResponseEntity<?> approveInstructor(Long accountId, Account reviewerAccount, String notes) {
        try {
            logger.info("Starting approval process for instructor account ID: {}", accountId);

            // Tìm tài khoản theo ID
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

            // Kiểm tra xem tài khoản này có phải giảng viên không
            boolean isInstructor = account.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("INSTRUCTOR"));
            if (!isInstructor) {
                logger.warn("Account ID {} is not an instructor", accountId);
                return ResponseEntity.badRequest().body(new MessageDTO("Account is not an instructor."));
            }

            // Kiểm tra trạng thái hiện tại của tài khoản
            if (account.getStatus() != EAccountStatus.PENDING) {
                logger.warn("Instructor account ID {} is not in 'PENDING' status", accountId);
                return ResponseEntity.badRequest().body(new MessageDTO("Instructor account is not in 'PENDING' status."));
            }

            // Cập nhật trạng thái tài khoản thành ACTIVE
            account.setStatus(EAccountStatus.ACTIVE);
            accountRepository.save(account);
            logger.info("Account status updated to ACTIVE for account ID: {}", accountId);

            // Tìm instructor liên kết với account này
            Instructor instructor = instructorRepository.findByAccountId(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found for account id: " + accountId));

            // Tạm thời vô hiệu hóa việc tạo Wallet
            boolean createWallet = false; // Đặt thành false để bỏ qua việc tạo wallet

            if (createWallet) {
                try {
                    // Khởi tạo ví cho instructor nếu chưa có
                    if (instructor.getWallet() == null) {
                        logger.info("Creating wallet for instructor ID: {}", instructor.getId());
                        Wallet wallet = new Wallet();
                        wallet.setBalance(BigDecimal.ZERO);
                        wallet.setInstructor(instructor);
                        instructor.setWallet(wallet);
                        instructorRepository.save(instructor);
                        logger.info("Wallet created successfully for instructor ID: {}", instructor.getId());
                    } else {
                        logger.info("Instructor ID: {} already has a wallet", instructor.getId());
                    }
                } catch (Exception e) {
                    // Ghi log lỗi nhưng không làm gián đoạn quy trình phê duyệt
                    logger.error("Error creating wallet for instructor ID {}: {}", instructor.getId(), e.getMessage());
                    logger.debug("Wallet creation error details", e);
                }
            } else {
                logger.info("Wallet creation is disabled. Skipping wallet creation for instructor ID: {}", instructor.getId());
            }

            // Gửi thông báo trong hệ thống
            try {
                String notificationContent = "Congratulations! Your instructor application has been approved. You can now create and publish courses.";
                notificationService.sendNotification(accountId, notificationContent, NotificationType.InstApprv, null, null);
                logger.info("Approval notification sent to account ID: {}", accountId);
            } catch (Exception e) {
                logger.error("Failed to send approval notification: {}", e.getMessage());
            }

            // Gửi email thông báo
            try {
                String emailContent = generateApprovalEmailContent(account.getUsername(), notes);
                emailService.sendEmail(account.getEmail(), "Instructor Application Approved", emailContent);
                logger.info("Approval email sent to: {}", account.getEmail());
            } catch (MessagingException e) {
                logger.error("Failed to send approval email to instructor: {}", e.getMessage());
                // Không throw exception ở đây để không làm gián đoạn quy trình phê duyệt
            }

            // Log hành động
            logger.info("Instructor account {} approved by admin {}", accountId, reviewerAccount.getUsername());
            return ResponseEntity.ok(new MessageDTO("Instructor account approved successfully."));
        } catch (ResourceNotFoundException e) {
            logger.error("Error approving instructor: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageDTO(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error approving instructor: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageDTO("Error approving instructor: " + e.getMessage()));
        }
    }

    /**
     * Từ chối tài khoản instructor
     * @param accountId ID của tài khoản cần từ chối
     * @param reviewerAccount Tài khoản admin thực hiện từ chối
     * @param rejectReason Lý do từ chối
     * @return ResponseEntity chứa thông báo kết quả
     */
    public ResponseEntity<?> rejectInstructor(Long accountId, Account reviewerAccount, String rejectReason) {
        try {
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

            // Gửi thông báo trong hệ thống
            String notificationContent = "Your instructor application has been rejected. Reason: " +
                    (rejectReason != null && !rejectReason.isEmpty() ? rejectReason : "No reason provided");
            notificationService.sendNotification(accountId, notificationContent, NotificationType.InstRejct, null, null);

            // Gửi email thông báo
            try {
                String emailContent = generateRejectionEmailContent(account.getUsername(), rejectReason);
                emailService.sendEmail(account.getEmail(), "Instructor Application Rejected", emailContent);
            } catch (MessagingException e) {
                logger.error("Failed to send rejection email to instructor: {}", e.getMessage());
                // Không throw exception ở đây để không làm gián đoạn quy trình từ chối
            }

            // Log hành động
            logger.info("Instructor account {} rejected by admin {}. Reason: {}",
                    accountId, reviewerAccount.getUsername(), rejectReason);

            return ResponseEntity.ok(new MessageDTO("Instructor account rejected. Reason: " + rejectReason));
        } catch (ResourceNotFoundException e) {
            logger.error("Error rejecting instructor: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageDTO(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error rejecting instructor: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageDTO("Error rejecting instructor: " + e.getMessage()));
        }
    }

    /**
     * Tạo nội dung email phê duyệt
     */
    private String generateApprovalEmailContent(String username, String notes) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");
        content.append("<h2>Congratulations! Your Instructor Application Has Been Approved</h2>");
        content.append("<p>Dear ").append(username).append(",</p>");
        content.append("<p>We are pleased to inform you that your application to become an instructor on our platform has been approved.</p>");
        content.append("<p>You can now create and publish courses to share your knowledge with our community.</p>");

        if (notes != null && !notes.isEmpty()) {
            content.append("<p><strong>Admin notes:</strong> ").append(notes).append("</p>");
        }

        content.append("<h3>Next Steps:</h3>");
        content.append("<ol>");
        content.append("<li>Log in to your account</li>");
        content.append("<li>Complete your instructor profile</li>");
        content.append("<li>Start creating your first course</li>");
        content.append("</ol>");

        content.append("<p>If you have any questions, please don't hesitate to contact our support team.</p>");
        content.append("<p>Best regards,<br>The Admin Team</p>");
        content.append("</body></html>");

        return content.toString();
    }

    /**
     * Tạo nội dung email từ chối
     */
    private String generateRejectionEmailContent(String username, String reason) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");
        content.append("<h2>Instructor Application Status Update</h2>");
        content.append("<p>Dear ").append(username).append(",</p>");
        content.append("<p>Thank you for your interest in becoming an instructor on our platform.</p>");
        content.append("<p>After careful review, we regret to inform you that your application has not been approved at this time.</p>");

        content.append("<p><strong>Reason for rejection:</strong> ");
        if (reason != null && !reason.isEmpty()) {
            content.append(reason);
        } else {
            content.append("Your application did not meet our current requirements.");
        }
        content.append("</p>");

        content.append("<p>You may reapply after addressing the issues mentioned above.</p>");
        content.append("<p>If you have any questions or need further clarification, please contact our support team.</p>");
        content.append("<p>Best regards,<br>The Admin Team</p>");
        content.append("</body></html>");

        return content.toString();
    }
}