// src/main/java/com/mytech/virtualcourse/services/AccountService.java
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.enums.RoleName;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.AccountMapper;
import com.mytech.virtualcourse.mappers.InstructorMapper; // Thêm InstructorMapper
import com.mytech.virtualcourse.mappers.StudentMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.RoleRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;

    private final RoleRepository roleRepository;
    private final AccountMapper accountMapper;
    private final StudentMapper studentMapper;
    private final InstructorMapper instructorMapper; // Thêm InstructorMapper

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          InstructorRepository instructorRepository,
                          StudentRepository studentRepository,
                          RoleRepository roleRepository,
                          AccountMapper accountMapper,
                          StudentMapper studentMapper,
                          InstructorMapper instructorMapper) { // Thêm InstructorMapper vào constructor
        this.accountRepository = accountRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.accountMapper = accountMapper;
        this.studentMapper = studentMapper;
        this.instructorMapper = instructorMapper; // Khởi tạo InstructorMapper
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        // Convert từ DTO sang Entity
        Account account = accountMapper.accountDTOToAccount(accountDTO);
        account.setAuthenticationType(AuthenticationType.valueOf(accountDTO.getAuthenticationType()));

        // Mã hoá password:
        if (accountDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        } else {
            throw new IllegalArgumentException("Password must not be empty");
        }

        // Thiết lập roles
        if (accountDTO.getRoles() != null && !accountDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (RoleName roleName : accountDTO.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                roles.add(role);
            }
            account.setRoles(roles);
        }else {
            // Tùy logic, có thể ném lỗi "User must have at least 1 role"
            throw new IllegalArgumentException("No roles selected");
        }

        // Lưu
        Account savedAccount = accountRepository.save(account);
        return accountMapper.accountToAccountDTO(savedAccount);
    }

    @Transactional
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return accountMapper.accountToAccountDTO(account);
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::accountToAccountDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    /**
     * Thêm Instructor vào Account, đảm bảo Account có ROLE_INSTRUCTOR
     */
    @Transactional
    public InstructorDTO addInstructorToAccount(Long accountId, InstructorDTO instructorDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Kiểm tra xem Account đã có Instructor chưa
        if (account.getInstructor() != null) {
            throw new RuntimeException("Account already has an Instructor.");
        }

        // Kiểm tra xem Account có ROLE_INSTRUCTOR không
        boolean hasInstructorRole = account.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.INSTRUCTOR);
        if (!hasInstructorRole) {
            throw new RuntimeException("Account does not have the ROLE_INSTRUCTOR role.");
        }

        // Tạo Instructor mới từ DTO
        Instructor instructor = new Instructor();
        instructor.setFirstName(instructorDTO.getFirstName());
        instructor.setLastName(instructorDTO.getLastName());
        instructor.setGender(Gender.valueOf(String.valueOf(instructorDTO.getGender())));
        instructor.setAddress(instructorDTO.getAddress());
        instructor.setPhone(instructorDTO.getPhone());
        instructor.setBio(instructorDTO.getBio());
        instructor.setPhoto(instructorDTO.getPhoto());
        instructor.setTitle(instructorDTO.getTitle());
        instructor.setWorkplace(instructorDTO.getWorkplace());
        instructor.setStatus(instructorDTO.getStatus());
        instructor.setAccount(account);

        // Liên kết Instructor với Account
        account.setInstructor(instructor);

        instructorRepository.save(instructor);
        accountRepository.save(account);

        // Sử dụng InstructorMapper để chuyển đổi Instructor thành InstructorDTO
        return instructorMapper.instructorToInstructorDTO(instructor);
    }

    /**
     * Thêm Student vào Account, đảm bảo Account có ROLE_STUDENT
     */
    @Transactional
    public StudentDTO addStudentToAccount(Long accountId, StudentDTO studentDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Kiểm tra xem Account đã có Student chưa
        if (account.getStudent() != null) {
            throw new RuntimeException("Account already has a Student.");
        }

        // Kiểm tra xem Account có ROLE_STUDENT không
        boolean hasStudentRole = account.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.STUDENT);
        if (!hasStudentRole) {
            throw new RuntimeException("Account does not have the ROLE_STUDENT role.");
        }

        // Tạo Student mới từ DTO
        Student student = new Student();
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setDob(studentDTO.getDob());
        student.setAddress(studentDTO.getAddress());
        student.setPhone(studentDTO.getPhone());
        student.setAvatar(studentDTO.getAvatar());
//        student.setVerifiedPhone(studentDTO.getVerifiedPhone());
        student.setCategoryPrefer(studentDTO.getCategoryPrefer());
        student.setStatusStudent(studentDTO.getStatusStudent());
        student.setAccount(account);

        // Liên kết Student với Account
        account.setStudent(student);

        studentRepository.save(student);
        accountRepository.save(account);

        // Sử dụng StudentMapper để chuyển đổi Student thành StudentDTO
        return studentMapper.studentToStudentDTO(student);
    }

    // Các phương thức để disable và enable Account
    @Transactional
    public void disableAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus("inactive");
        accountRepository.save(account);
    }

    @Transactional
    public void enableAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus("active");
        accountRepository.save(account);
    }

    @Transactional
    public void assignRoleToAccount(Long accountId, RoleName roleName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        account.getRoles().add(role);
        accountRepository.save(account);
    }

    /**
     * Loại bỏ một vai trò khỏi Account.
     */
    @Transactional
    public void removeRoleFromAccount(Long accountId, RoleName roleName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        if (account.getRoles().contains(role)) {
            account.getRoles().remove(role);
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Account does not have the role: " + roleName);
        }
    }

    @Transactional
    public AccountDTO updateUser(Long id, AccountDTO accountDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setUsername(accountDTO.getUsername());
        account.setEmail(accountDTO.getEmail());
        account.setType(accountDTO.getType());
        account.setStatus(accountDTO.getStatus());

        // Nếu có cập nhật mật khẩu, mã hóa mật khẩu mới
        if (accountDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        }

        Account updatedAccount = accountRepository.save(account);
        return accountMapper.accountToAccountDTO(updatedAccount);
    }

    @Transactional
    public void forgotPassword(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with email: " + email));

        // Tạo token đặt lại mật khẩu (có thể sử dụng JWT hoặc UUID)
        String resetToken = UUID.randomUUID().toString();

        // Lưu token vào cơ sở dữ liệu hoặc sử dụng cache
        // Ví dụ: thêm một trường resetPasswordToken vào Account và cập nhật
        account.setResetPasswordToken(resetToken);
        accountRepository.save(account);

        // Gửi email chứa đường dẫn đặt lại mật khẩu tới người dùng
        // Ví dụ: sử dụng JavaMailSender hoặc một dịch vụ email bên ngoài
        String resetPasswordLink = "http://localhost:3000/reset-password?token=" + resetToken;
        sendResetPasswordEmail(account.getEmail(), resetPasswordLink);
    }

    private void sendResetPasswordEmail(String email, String resetPasswordLink) {
        // Implement việc gửi email ở đây
        // Bạn có thể sử dụng JavaMailSender hoặc tích hợp với các dịch vụ gửi email bên ngoài như SendGrid, Mailgun
        System.out.println("Gửi email đến: " + email + " với đường dẫn đặt lại mật khẩu: " + resetPasswordLink);
    }
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Account account = accountRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset password token."));

        account.setPassword(passwordEncoder.encode(newPassword));
        account.setResetPasswordToken(null); // Xóa token sau khi đặt lại mật khẩu
        accountRepository.save(account);

        // Gửi thông báo email cho người dùng về việc thay đổi mật khẩu thành công (tùy chọn)
        sendPasswordResetConfirmationEmail(account.getEmail());
    }

    private void sendPasswordResetConfirmationEmail(String email) {
        // Implement việc gửi email xác nhận thay đổi mật khẩu
        System.out.println("Gửi email xác nhận thay đổi mật khẩu đến: " + email);
    }
}
