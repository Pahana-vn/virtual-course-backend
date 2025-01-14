package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.RegisterDTO;
import com.mytech.virtualcourse.dtos.LoginDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.dtos.JwtDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.RoleRepository;
import com.mytech.virtualcourse.security.CustomUserDetails;
import com.mytech.virtualcourse.security.JwtUtil;
import com.mytech.virtualcourse.mappers.AccountMapper;
import com.mytech.virtualcourse.mappers.JwtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
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
    private JwtUtil jwtUtil;

    @Autowired
    private JwtMapper jwtMapper;

    @Autowired
    private AccountMapper accountMapper;

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

        Account account = new Account();
        account.setUsername(registerRequest.getUsername());
        account.setEmail(registerRequest.getEmail());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setVerifiedEmail(false);
        account.setVersion(1);
        account.setAuthenticationType(AuthenticationType.LOCAL);

        Set<Role> roles = new HashSet<>();

        if (registerRequest.getRole().equalsIgnoreCase("admin")) {
            if (accountRepository.findAll().stream()
                    .anyMatch(a -> a.getRoles().stream()
                            .anyMatch(r -> r.getName().equals("ADMIN")))) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageDTO("Error: Admin account already exists!"));
            }
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
            roles.add(adminRole);
            account.setStatus(EAccountStatus.ACTIVE);
            account.setVerifiedEmail(true);
        } else if (registerRequest.getRole().equalsIgnoreCase("instructor")) {
            Role instructorRole = roleRepository.findByName("INSTRUCTOR")
                    .orElseThrow(() -> new RuntimeException("Error: Role INSTRUCTOR is not found."));
            roles.add(instructorRole);
            account.setStatus(EAccountStatus.PENDING);
        } else {
            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> new RuntimeException("Error: Role STUDENT is not found."));
            roles.add(studentRole);
            account.setStatus(EAccountStatus.ACTIVE);
        }

        account.setRoles(new ArrayList<>(roles));

        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("User registered successfully!"));
    }

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDTO("Error: Invalid username or password"));
        }
    }
}
