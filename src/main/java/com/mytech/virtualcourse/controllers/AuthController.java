package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.JwtDTO;
import com.mytech.virtualcourse.dtos.LoginDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.dtos.RegisterDTO;
import com.mytech.virtualcourse.mappers.JwtMapper;
import com.mytech.virtualcourse.security.CustomUserDetails;
import com.mytech.virtualcourse.security.JwtUtil;
import com.mytech.virtualcourse.services.AuthService;
import com.mytech.virtualcourse.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
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


}
