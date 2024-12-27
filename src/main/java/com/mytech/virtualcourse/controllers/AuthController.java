package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.enums.RoleName;
import com.mytech.virtualcourse.security.JwtUtils;
import com.mytech.virtualcourse.security.AccountDetailsImpl;
import com.mytech.virtualcourse.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountService accountService;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Đăng ký tài khoản (học viên hoặc giảng viên)
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        try {
            AccountDTO accountDTO = accountService.createAccount(signUpRequestToAccountDTO(signUpRequest));

            // Nếu đăng ký với vai trò INSTRUCTOR hoặc STUDENT, xử lý thêm thông tin tương ứng
            if (signUpRequest.getRoles() != null) {
                if (signUpRequest.getRoles().contains(com.mytech.virtualcourse.enums.RoleName.INSTRUCTOR)) {
                    // Chuyển hướng để thêm thông tin giảng viên
                    return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công! Vui lòng thêm thông tin giảng viên.");
                }
                if (signUpRequest.getRoles().contains(com.mytech.virtualcourse.enums.RoleName.STUDENT)) {
                    // Chuyển hướng để thêm thông tin sinh viên
                    return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công! Vui lòng thêm thông tin sinh viên.");
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký tài khoản thành công!");
        } catch (Exception e) {
            // Ghi log chi tiết
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Đăng nhập
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            AccountDetailsImpl userDetails = (AccountDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            JwtResponse jwtResponse = new JwtResponse(jwt, "Bearer", userDetails.getId(),
                    userDetails.getUsername(), userDetails.getEmail(), roles);

            return ResponseEntity.ok(jwtResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password.");
        } catch (Exception e) {
            e.printStackTrace(); // log full error

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Quên mật khẩu
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            accountService.forgotPassword(forgotPasswordRequest.getEmail());
            return ResponseEntity.ok("Đường dẫn đặt lại mật khẩu đã được gửi đến email của bạn.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin người dùng
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            AccountDTO accountDTO = accountService.getAccountById(id);
            return ResponseEntity.ok(accountDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    /**
     * Chỉnh sửa thông tin người dùng
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            AccountDTO updatedAccount = accountService.updateUser(id, updateUserRequestToAccountDTO(updateUserRequest));
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Đặt lại mật khẩu
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            accountService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
            return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Chuyển đổi RegisterRequest sang AccountDTO
     */
    private AccountDTO signUpRequestToAccountDTO(RegisterRequest request) {
        AccountDTO dto = new AccountDTO();
        dto.setUsername(request.getUsername());
        dto.setEmail(request.getEmail());
        dto.setPassword(request.getPassword());
        dto.setRoles(request.getRoles());
        dto.setEnable(true);
        dto.setVerifiedEmail(false);
        dto.setAuthenticationType("LOCAL");
        if (request.getRoles().contains(RoleName.ADMIN)) {
            dto.setType("ADMIN_ACCOUNT");
        } else if (request.getRoles().contains(RoleName.INSTRUCTOR)) {
            dto.setType("INSTRUCTOR_ACCOUNT");
        } else if (request.getRoles().contains(RoleName.STUDENT)) {
            dto.setType("STUDENT_ACCOUNT");
        } else {
            dto.setType("USER_ACCOUNT");
        }
        dto.setStatus("active");
        return dto;
    }

    /**
     * Chuyển đổi UpdateUserRequest sang AccountDTO
     */
    private AccountDTO updateUserRequestToAccountDTO(UpdateUserRequest request) {
        AccountDTO dto = new AccountDTO();
        dto.setUsername(request.getUsername());
        dto.setEmail(request.getEmail());
//        dto.setType(request.getType());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
