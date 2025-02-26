package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.mappers.JwtMapper;
import com.mytech.virtualcourse.security.CustomUserDetails;
import com.mytech.virtualcourse.security.JwtUtil;
import com.mytech.virtualcourse.services.AuthService;
import com.mytech.virtualcourse.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
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


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerRequest) {
        return authService.registerUser(registerRequest);
    }



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken((CustomUserDetails) authentication.getPrincipal());

            // Dùng CookieUtil để lưu cookie
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

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Missing or invalid Authorization header");
        }
        try {
            String jwtToken = token.replace("Bearer ", "");
            // Lấy email từ token
            String email = jwtUtil.getEmailFromJwtToken(jwtToken);
            Account user = authService.findByEmail(email);

            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }

            return ResponseEntity.ok(user);
        }catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body("Token has expired");
        }catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token or user not found");
        }
    }
}
