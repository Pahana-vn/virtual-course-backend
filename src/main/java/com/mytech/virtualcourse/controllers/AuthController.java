package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.configs.security.jwt.JwtUtils;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.RefreshToken;
import com.mytech.virtualcourse.exceptions.TokenRefreshException;
import com.mytech.virtualcourse.payload.request.LoginRequest;
import com.mytech.virtualcourse.payload.response.JwtResponse;
import com.mytech.virtualcourse.payload.response.MessageResponse;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import com.mytech.virtualcourse.services.RefreshTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.mytech.virtualcourse.configs.security.services.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "Auth")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    InstructorRepository instructorRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("LoginRequest: " + loginRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Optional<Account> accountOptional = accountRepository.findById(userDetails.getId());
        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account not found.");
        }
        Account account = accountOptional.get();

        if (!account.getEnable()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account has been banned. Please contact the provider.");

        account.setEnable(true);
        accountRepository.save(account);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        JwtResponse jwtResponse = JwtResponse.builder()
                        .id(userDetails.getId())
                        .token(jwt)
                        .username(account.getUsername())
                        .firstName(jwt)
                        .email(userDetails.getEmail())
                        .type("Bearer")
                        .roles(roles)
                        .createdAt(account.getCreatedAt())
                        .build();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_INSTRUCTOR"))) {
            Instructor instructor = instructorRepository.findByAccountId(account.getId()); // Tìm Instructor theo Account
            if (instructor != null) {
                jwtResponse.setDataFromInstructor(instructor);// Gán thông tin từ Instructor
            }
        } else if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT"))) {
            Student student = studentRepository.findByAccountId(account.getId()); // Tìm Student theo Account
            if (student != null) {
                jwtResponse.setDataFromStudent(student); // Gán thông tin từ Student
            }
        }

        if (jwtResponse.getFirstName() != null && jwtResponse.getLastName() != null) {
            jwtResponse.setFullname(jwtResponse.getFirstName() + " " + jwtResponse.getLastName());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(jwtResponse);

    }
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);

        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String token = jwtUtils.generateTokenFromEmail(user.getEmail());
                        return ResponseEntity.ok(JwtResponse.builder()
                                .id(user.getId())
                                .fullname(getFullName(user))
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .image(getImage(user))
                                .createdAt(getCreatedAt(user))
                                .token(token)
                                .type("Bearer")
                                .roles(user.getRoles().stream().map(role -> role.getName().name()).toList())
                                .build());
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> validationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    private String getFullName(Object user) {
        if (user instanceof Student student) {
            return student.getFirstName() + " " + student.getLastName();
        } else if (user instanceof Instructor instructor) {
            return instructor.getFirstName() + " " + instructor.getLastName();
        }
        return null;
    }

    private String getImage(Object user) {
        if (user instanceof Student student) {
            return student.getAvatar() != null ? student.getAvatar() : "default-avatar.png";
        } else if (user instanceof Instructor instructor) {
            return instructor.getPhoto() != null ? instructor.getPhoto() : "default-photo.png";
        }
        return "default-image.png";
    }

    private LocalDateTime getCreatedAt(Object user) {
        if (user instanceof Student student) {
            return student.getCreatedAt();
        } else if (user instanceof Instructor instructor) {
            return instructor.getCreatedAt();
        }
        return null;
    }
}
