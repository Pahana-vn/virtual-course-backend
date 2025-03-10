package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.StatusWallet;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.AccountMapper;
import com.mytech.virtualcourse.mappers.InstructorMapper;
import com.mytech.virtualcourse.mappers.JwtMapper;
import com.mytech.virtualcourse.repositories.*;
import com.mytech.virtualcourse.security.CustomUserDetails;
import com.mytech.virtualcourse.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
    private InstructorMapper instructorMapper;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private SocialRepository socialRepository;
    @Autowired
    private StudentRepository studentRepository;

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

        // Create Account
        Account account = new Account();
        account.setUsername(registerRequest.getUsername());
        account.setEmail(registerRequest.getEmail());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setVerifiedEmail(false);
        account.setVersion(1);
        account.setAuthenticationType(AuthenticationType.LOCAL);

        Set<Role> roles = new HashSet<>();

        // Assign roles based on user selection (admin, instructor, or student)
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
            // Register as a student
            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> new RuntimeException("Error: Role STUDENT is not found."));
            roles.add(studentRole);
            account.setStatus(EAccountStatus.ACTIVE);

            // Create Student and link to Account
            Student student = new Student();
            student.setFirstName("");  // Empty or default values
            student.setLastName("");
            student.setDob(null);  // No dob initially
            student.setGender(null);  // Gender will be updated later
            student.setStatusStudent("ACTIVE");
            student.setAccount(account);  // Link Student to Account
            account.setStudent(student);  // Link Account to Student
        }

        account.setRoles(new ArrayList<>(roles));

        // Save Account (this will cascade and also save the Student if role is Student)
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("User registered successfully!"));
    }

    public ResponseEntity<?> registerInstructor(InstructorRegistrationDTO registrationDTO) {

        // 1. Tạo tài khoản người dùng (Account)
        Account account = new Account();
        account.setUsername(registrationDTO.getUsername());
        account.setEmail(registrationDTO.getEmail());
        account.setPassword(passwordEncoder.encode(registrationDTO.getPassword())); // Mã hóa mật khẩu
        account.setAuthenticationType(registrationDTO.getAuthenticationType() != null ?
                registrationDTO.getAuthenticationType() : AuthenticationType.LOCAL);
        account.setVersion(registrationDTO.getVersion());
        account.setStatus(EAccountStatus.PENDING);

        Role instructorRole = roleRepository.findByName("INSTRUCTOR")
                .orElseThrow(() -> new RuntimeException("Role not found: INSTRUCTOR"));
        account.setRoles(Collections.singletonList(instructorRole));

        account = accountRepository.save(account);

        // 2. Tạo Instructor
        Instructor instructor = new Instructor();
        instructor.setFirstName(registrationDTO.getFirstName());
        instructor.setLastName(registrationDTO.getLastName());
        instructor.setGender(registrationDTO.getGender());
        instructor.setAddress(registrationDTO.getAddress());
        instructor.setPhone(registrationDTO.getPhone());
        instructor.setBio(registrationDTO.getBio());
        instructor.setPhoto(registrationDTO.getPhoto());
        instructor.setTitle(registrationDTO.getTitle());
        instructor.setWorkplace(registrationDTO.getWorkplace());
        instructor.setAccount(account);
        instructor = instructorRepository.save(instructor);

        // 3. Tạo Wallet (Thêm các trường mới: balance, minLimit, statusWallet, lastUpdated)
        Wallet wallet = new Wallet();
        wallet.setInstructor(instructor);
        wallet.setBalance(registrationDTO.getBalance() != null ? registrationDTO.getBalance() : BigDecimal.ZERO);
        wallet.setMinLimit(registrationDTO.getMinLimit() != null ? registrationDTO.getMinLimit() : BigDecimal.valueOf(1000));
        wallet.setStatusWallet(registrationDTO.getStatusWallet() != null ? registrationDTO.getStatusWallet() : StatusWallet.ACTIVE);
        wallet.setLastUpdated(registrationDTO.getLastUpdated() != null ? registrationDTO.getLastUpdated() : new Timestamp(System.currentTimeMillis()));
        wallet = walletRepository.save(wallet);

        // 4. Tạo Social Info
        Social social = new Social();
        social.setInstructor(instructor);
        social.setFacebookUrl(registrationDTO.getFacebookUrl());
        social.setGoogleUrl(registrationDTO.getGoogleUrl());
        social.setInstagramUrl(registrationDTO.getInstagramUrl());
        social.setLinkedinUrl(registrationDTO.getLinkedinUrl());
        socialRepository.save(social);

        // 5. Map và trả về InstructorDTO
        instructorMapper.instructorToInstructorDTO(instructor);
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
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // ✅ Lấy accountId và studentId
            Long accountId = userDetails.getAccountId();
            Long studentId = userDetails.getStudentId();
            Long instructorId = userDetails.getInstructorId();

            // ✅ Kiểm tra accountId có null không
            if (accountId == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new MessageDTO("Error: Account ID is null!"));
            }

            // ✅ Tạo JWT Token
            String jwt = jwtUtil.generateJwtToken(userDetails);

            // ✅ Set dữ liệu cho JwtDTO
            JwtDTO jwtDTO = jwtMapper.toJwtDTO(userDetails);
            jwtDTO.setToken(jwt);
            jwtDTO.setAccountId(accountId);
            jwtDTO.setStudentId(studentId);

            return ResponseEntity.ok(jwtDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDTO("Error: Invalid username or password"));
        }
    }

    public String getInstructorAvatar(Long accountId) {

        if (!instructorRepository.existsInstructorByAccountId(accountId)) {
            throw new ResourceNotFoundException("Instructor not found with account id: " + accountId);
        }

        Optional<Instructor> optionalInstructor = instructorRepository.findByAccountId(accountId);

        if (optionalInstructor.isPresent()) {
            return optionalInstructor.get().getPhoto();
        } else {
            throw new ResourceNotFoundException("Instructor not found with account id: " + accountId);
        }
    }

    public String getStudentAvatar(Long accountId) {
        if (!studentRepository.existsStudentByAccountId(accountId)) {
            throw new ResourceNotFoundException("Student not found with account id: " + accountId);
        }
        Optional<Student> optionalStudent = studentRepository.findByAccountId(accountId);

        if (optionalStudent.isPresent()) {
            return optionalStudent.get().getAvatar();
        } else {
            throw new ResourceNotFoundException("Student not found with account id: " + accountId);
        }
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElse(null);
    }
}
