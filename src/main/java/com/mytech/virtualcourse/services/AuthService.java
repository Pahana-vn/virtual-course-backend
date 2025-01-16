// src/main/java/com/mytech/virtualcourse/services/AuthService.java
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ERole;
import com.mytech.virtualcourse.mappers.*;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.RoleRepository;
import com.mytech.virtualcourse.repositories.WalletRepository;
import com.mytech.virtualcourse.security.CustomUserDetails;
import com.mytech.virtualcourse.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private InstructorMapper instructorMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private JwtUtil jwtUtil; // Giả sử bạn có JwtUtil để tạo token

    @Autowired
    private JwtMapper jwtMapper;

    @Autowired
    private InstructorService instructorService;
    /**
     * Đăng ký người dùng mới.
     *
     * @param registerRequest Dữ liệu đăng ký.
     * @return Phản hồi về kết quả đăng ký.
     */
    public ResponseEntity<?> registerUser(RegisterDTO registerRequest) {
        // Kiểm tra username hoặc email đã tồn tại
        if (accountRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageDTO("Error: Username is already taken!"));
        }

        if (accountRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageDTO("Error: Email is already in use!"));
        }

        // Chuyển DTO sang Entity
        Account account = accountMapper.registerDTOToAccount(registerRequest);

        // Mã hóa password
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Thiết lập vai trò
        Set<Role> roles = new HashSet<>();

        if (registerRequest.getRole().equalsIgnoreCase("instructor")) {
            Role instructorRole = roleRepository.findByName(String.valueOf(ERole.INSTRUCTOR))
                    .orElseThrow(() -> new RuntimeException("Error: Role INSTRUCTOR is not found."));
            roles.add(instructorRole);
            account.setStatus(EAccountStatus.PENDING); // Chờ Admin duyệt
        } else if (registerRequest.getRole().equalsIgnoreCase("student")) {
            Role studentRole = roleRepository.findByName(String.valueOf(ERole.STUDENT))
                    .orElseThrow(() -> new RuntimeException("Error: Role STUDENT is not found."));
            roles.add(studentRole);
            account.setStatus(EAccountStatus.ACTIVE);
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageDTO("Error: Invalid role selected."));
        }

        account.setRoles(new ArrayList<>(roles));

        // Thiết lập các trường mặc định
        account.setVerifiedEmail(false);
        account.setAuthenticationType(AuthenticationType.LOCAL);
        account.setVersion(1);

        // Lưu tài khoản
        Account savedAccount = accountRepository.save(account);

        // Nếu là Instructor, tạo thông tin bổ sung với trạng thái PENDING
        if (registerRequest.getRole().equalsIgnoreCase("instructor")) {
            InstructorDTO instructorDTO = new InstructorDTO();
            instructorDTO.setAccountId(savedAccount.getId());
            // Các trường khác có thể được điền sau khi Admin duyệt hoặc để trống
            instructorService.createInstructor(instructorDTO);
        }

        return ResponseEntity.ok(new MessageDTO("User registered successfully! Please wait for approval."));
    }

    /**
     * Xác thực người dùng và tạo JWT token.
     *
     * @param loginRequest Dữ liệu đăng nhập.
     * @return JWT token hoặc lỗi xác thực.
     */
    public ResponseEntity<?> authenticateUser(LoginDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken((CustomUserDetails) authentication.getPrincipal());

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            JwtDTO jwtDTO = jwtMapper.toJwtDTO(userDetails);
            jwtDTO.setToken(jwt);

            return ResponseEntity.ok(jwtDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(new MessageDTO("Error: Invalid email or password"));
        }
    }
}
