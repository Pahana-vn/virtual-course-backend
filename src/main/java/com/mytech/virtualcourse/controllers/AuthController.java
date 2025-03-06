package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.mappers.JwtMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.security.CustomUserDetails;
import com.mytech.virtualcourse.security.JwtUtil;
import com.mytech.virtualcourse.services.AuthService;
import com.mytech.virtualcourse.services.EmailVerificationService;
import com.mytech.virtualcourse.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://10.0.2.2:8080"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtMapper jwtMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EmailVerificationService emailVerificationService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerRequest) {
        return authService.registerUser(registerRequest);
    }

    @PostMapping("instructor/register")
    public ResponseEntity<?> registerInstructor(@RequestBody InstructorRegistrationDTO registrationDTO) {
        return authService.registerInstructor(registrationDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginRequest, HttpServletResponse response) {
        try {
            Account account = accountRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Email does not exist"));

            if (account.getStatus() == EAccountStatus.PENDING) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageDTO("Your account is pending approval. Please wait for admin approval."));
            }

            if (!account.getVerifiedEmail()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageDTO("Email not verified. Please check your inbox."));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken((CustomUserDetails) authentication.getPrincipal());

            CookieUtil.addTokenCookie(response, jwt);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            JwtDTO jwtDTO = jwtMapper.toJwtDTO(userDetails);
            jwtDTO.setToken(jwt);

            return ResponseEntity.ok(jwtDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDTO("Error: Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        // Dùng CookieUtil để xóa cookie
        CookieUtil.clearTokenCookie(response);

        // Xóa session authentication
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new MessageDTO("User logged out successfully!"));
    }

    @GetMapping("/{accountId}/instructor-avatar")
    public ResponseEntity<Map<String, String>> getInstructorAvatar(@PathVariable Long accountId) {
        String avatarFileName = authService.getInstructorAvatar(accountId);

        if (avatarFileName == null || avatarFileName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Avatar not found"));
        }

        String avatarUrl = "http://localhost:8080/uploads/instructor/" + avatarFileName;

        // Trả về đối tượng JSON chứa URL
        Map<String, String> response = new HashMap<>();
        response.put("url", avatarUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/student-avatar")
    public ResponseEntity<Map<String, String>> getStudentAvatar(@PathVariable Long accountId) {
        String avatarFileName = authService.getStudentAvatar(accountId);

        if (avatarFileName == null || avatarFileName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Avatar not found"));
        }

        String avatarUrl = "http://localhost:8080/uploads/student/" + avatarFileName;

        Map<String, String> response = new HashMap<>();
        response.put("url", avatarUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(400).body("Missing or invalid Authorization header");
            }

            String jwtToken = token.replace("Bearer ", "");
            if (jwtUtil.validateJwtToken(jwtToken)) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(401).body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token or other error");
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            if (account.getToken() != null && passwordEncoder.matches(token, account.getToken())) {
                if (account.getTokenExpiry().isBefore(LocalDateTime.now())) {
                    return ResponseEntity.badRequest().body("Token has expired. Please request a resend of verification email.");
                }

                account.setVerifiedEmail(true);
                account.setToken(null);
                account.setTokenExpiry(null);
                accountRepository.save(account);

                return ResponseEntity.ok("Account has been activated successfully!");
            }
        }

        return ResponseEntity.badRequest().body("Invalid Token!");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email does not exist."));

        if (account.getVerifiedEmail()) {
            return ResponseEntity.badRequest().body("Email has been previously verified.");
        }

        emailVerificationService.sendVerificationEmail(email);
        return ResponseEntity.ok("Verification email has been resent!");
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        boolean exists = accountRepository.existsByEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameExists(@RequestParam String username) {
        boolean exists = accountRepository.existsByUsername(username);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }
}
