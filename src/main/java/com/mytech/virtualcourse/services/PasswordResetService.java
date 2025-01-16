package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.PasswordResetRequestDTO;
import com.mytech.virtualcourse.dtos.ResetPasswordDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Yêu cầu reset mật khẩu
    public ResponseEntity<?> forgotPassword(PasswordResetRequestDTO request) {
        Optional<Account> accountOpt = accountRepository.findByEmail(request.getEmail());
        if (accountOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with email: " + request.getEmail());
        }

        Account account = accountOpt.get();

        // Tạo token reset mật khẩu
        String token = UUID.randomUUID().toString();

        // Đặt thời gian hết hạn cho token (ví dụ: 1 giờ)
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(1);

        // Lưu token và thời gian hết hạn vào tài khoản
        account.setResetPasswordToken(token);
        account.setResetPasswordTokenExpiry(expiryTime);
        accountRepository.save(account);

        // Gửi email reset mật khẩu
        sendResetPasswordEmail(account.getEmail(), token);

        return ResponseEntity.ok(new MessageDTO("Password reset link has been sent to your email."));
    }

    // Đặt lại mật khẩu
    public ResponseEntity<?> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Optional<Account> accountOpt = accountRepository.findByResetPasswordToken(resetPasswordDTO.getToken());
        if (accountOpt.isEmpty()) {
            throw new ResourceNotFoundException("Invalid reset token.");
        }

        Account account = accountOpt.get();

        // Kiểm tra token còn hiệu lực không
        if (account.getResetPasswordTokenExpiry() == null || account.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new MessageDTO("Reset token has expired."));
        }

        // Cập nhật mật khẩu mới
        account.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        // Xóa token sau khi sử dụng
        account.setResetPasswordToken(null);
        account.setResetPasswordTokenExpiry(null);
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("Password has been reset successfully."));
    }

    // Phương thức gửi email reset mật khẩu với Thymeleaf
    private void sendResetPasswordEmail(String to, String token) {
        String resetUrl = "http://localhost:8180/api/auth/reset-password?token=" + token;

        // Tạo context cho Thymeleaf
        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);

        String htmlContent = templateEngine.process("reset-password.html", context);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Reset Your Password");
            helper.setFrom("phamhoainhannn@gmail.com");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}