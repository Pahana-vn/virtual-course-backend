package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void sendVerificationEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email does not exist"));

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawToken);

        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);

        account.setToken(hashedToken);
        account.setTokenExpiry(expiryTime);
        accountRepository.save(account);

        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + rawToken;
        String subject = "Verify your account";
        String content = "Click the link to confirm email: " + verificationLink;

        sendEmail(account.getEmail(), subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
