package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.dtos.PasswordResetRequestDTO;
import com.mytech.virtualcourse.dtos.ResetPasswordDTO;
import com.mytech.virtualcourse.services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequestDTO request) {
        return passwordResetService.forgotPassword(request);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return passwordResetService.resetPassword(resetPasswordDTO);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> handleResetPasswordGet(@RequestParam("token") String token) {
        return ResponseEntity.badRequest().body(new MessageDTO("Please use an API client like Postman to reset your password. Use the token provided in this email to send a PUT request to /api/auth/reset-password with your new password."));
    }
}
