// src/main/java/com/mytech/virtualcourse/services/AccountService.java
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.enums.ERole;
import com.mytech.virtualcourse.enums.StatusWallet;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.*;
import com.mytech.virtualcourse.repositories.AdminAccountRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.RoleRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import com.mytech.virtualcourse.repositories.WalletRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final AdminAccountRepository accountRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final InstructorService instructorService; // Inject InstructorService
    private final WalletRepository walletRepository;
    private final RoleRepository roleRepository;
    private final AccountMapper accountMapper;
    private final StudentMapper studentMapper;
    private final InstructorMapper instructorMapper; // Thêm InstructorMapper
    private final WalletMapper walletMapper; // Thêm WalletMapper
    @Autowired
    private JwtMapper jwtMapper;

    public AccountService(AdminAccountRepository accountRepository,
            InstructorRepository instructorRepository,
            StudentRepository studentRepository,
            InstructorService instructorService,
            RoleRepository roleRepository,
            AccountMapper accountMapper,
            StudentMapper studentMapper,
            InstructorMapper instructorMapper,
            WalletMapper walletMapper,
            WalletRepository walletRepository) { // Thêm WalletMapper vào constructor
        this.accountRepository = accountRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.instructorService = instructorService;
        this.roleRepository = roleRepository;
        this.accountMapper = accountMapper;
        this.studentMapper = studentMapper;
        this.instructorMapper = instructorMapper;
        this.walletMapper = walletMapper; // Khởi tạo WalletMapper
        this.walletRepository = walletRepository; // Khởi tạo walletRepository
    }

    @Transactional
    public AccountDTO createAccount(LoginDTO loginDTO) {
        // Convert từ DTO sang Entity
        Account account = jwtMapper.toAccount(AccountDTO);
        account.setAuthenticationType(AuthenticationType.valueOf(accountDTO.getAuthenticationType()));

        // Mã hoá password:
        if (loginDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        } else {
            throw new IllegalArgumentException("Password must not be empty");
        }

        // Thiết lập roles
        if (accountDTO.getRoles() != null && !accountDTO.getRoles().isEmpty()) {
            Set<Role> rolesSet = new HashSet<>(); // Sử dụng Set để đảm bảo các vai trò là duy nhất

            // Lặp qua danh sách các vai trò từ DTO
            for (ERole roleName : accountDTO.getRoles()) {
                // Tìm vai trò từ repository
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                rolesSet.add(role); // Thêm vào Set để loại bỏ các giá trị trùng lặp
            }

            // Chuyển đổi từ Set sang List
            List<Role> rolesList = new ArrayList<>(rolesSet);

            account.setRoles(rolesList); // Gán danh sách vai trò vào tài khoản
        } else {
            // Ném lỗi nếu không có vai trò nào được chọn
            throw new IllegalArgumentException(
                    "User must have at least one role. Provided roles: " + accountDTO.getRoles());
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

        // Log các vai trò của tài khoản
        logger.info("Account ID: {}", accountId);
        logger.info("Account Roles: {}", account.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));

        // Kiểm tra xem Account đã có Instructor chưa
        if (account.getInstructor() != null) {
            logger.error("Account ID {} already has an Instructor.", accountId);
            throw new RuntimeException("Account already has an Instructor.");
        }

        // Kiểm tra xem Account có ROLE_INSTRUCTOR không
        boolean hasInstructorRole = account.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.INSTRUCTOR));
        if (!hasInstructorRole) {
            logger.error("Account ID {} does not have the INSTRUCTOR role.", accountId);
            throw new RuntimeException("Account does not have the INSTRUCTOR role.");
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
        instructor.setAccount(account);

        // Liên kết Instructor với Account
        account.setInstructor(instructor);
        // Tạo Wallet cho Student
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatusWallet(StatusWallet.ACTIVE);
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        wallet.setInstructor(instructor);

        instructor.setWallet(wallet);

        instructorRepository.save(instructor);
        walletRepository.save(wallet); // Đảm bảo Wallet được lưu
        accountRepository.save(account);
        // Sử dụng WalletMapper để chuyển đổi Wallet sang WalletDTO nếu cần
        WalletDTO walletDTO = walletMapper.walletToWalletDTO(wallet);
        instructorDTO.setWalletId(walletDTO.getId());
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
                .anyMatch(role -> role.getName() == ERole.STUDENT);
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
        // student.setVerifiedPhone(studentDTO.getVerifiedPhone());
        student.setCategoryPrefer(studentDTO.getCategoryPrefer());
//        student.setStatusStudent(studentDTO.getStatusStudent());
        student.setAccount(account);

        // Liên kết Student với Account
        account.setStudent(student);

        // Tạo Wallet cho Student
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatusWallet(StatusWallet.ACTIVE);
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        wallet.setStudent(student);

        student.setWallet(wallet);

        studentRepository.save(student);
        walletRepository.save(wallet); // Đảm bảo Wallet được lưu
        accountRepository.save(account);

        // Sử dụng WalletMapper để chuyển đổi Wallet sang WalletDTO nếu cần
        WalletDTO walletDTO = walletMapper.walletToWalletDTO(wallet);
        studentDTO.setWalletId(walletDTO.getId());
        // Sử dụng StudentMapper để chuyển đổi Student thành StudentDTO
        return studentMapper.studentToStudentDTO(student);
    }

    // Các phương thức để disable và enable Account
    @Transactional
    public void disableAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(EAccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public void enableAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(EAccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public void assignRoleToAccount(Long accountId, ERole roleName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        if (!account.getRoles().contains(role)) {
            account.getRoles().add(role);
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Account already has the role: " + roleName);
        }
    }

    /**
     * Loại bỏ một vai trò khỏi Account.
     */
    @Transactional
    public void removeRoleFromAccount(Long accountId, ERole roleName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        if (account.getRoles().contains(role)) {
            account.getRoles().remove(role);
            accountRepository.save(account);

            // Nếu loại bỏ vai trò INSTRUCTOR, xóa Instructor liên kết
            if (roleName == ERole.INSTRUCTOR && account.getInstructor() != null) {
                instructorService.deleteInstructor(account.getInstructor().getId());
                account.setInstructor(null);
                accountRepository.save(account);
            }
            // Nếu loại bỏ vai trò STUDENT, xóa Student liên kết
            if (roleName == ERole.STUDENT && account.getStudent() != null) {
                // Giả sử bạn có StudentService tương tự InstructorService
                // studentService.deleteStudent(account.getStudent().getId());
                account.setStudent(null);
                accountRepository.save(account);
            }
        } else {
            throw new RuntimeException("Account does not have the role: " + roleName);
        }
    }

    @Transactional
    public AccountDTO updateUser(Long id, AccountDTO accountDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Cập nhật các trường thông tin
        account.setUsername(accountDTO.getUsername());
        account.setEmail(accountDTO.getEmail());
        account.setStatus(EAccountStatus.valueOf(accountDTO.getStatus())); // Đã sửa: truyền giá trị EAccountStatus

        // Nếu có cập nhật mật khẩu, mã hóa mật khẩu mới
        if (accountDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        }

        // Cập nhật các trường khác nếu cần thiết
        account.setEnable(accountDTO.isEnable());
        account.setVerifiedEmail(accountDTO.isVerifiedEmail());
        account.setAuthenticationType(AuthenticationType.valueOf(accountDTO.getAuthenticationType()));
        account.setVersion(accountDTO.getVersion());

        // Cập nhật roles nếu có
        if (accountDTO.getRoles() != null && !accountDTO.getRoles().isEmpty()) {
            List<Role> rolesList = accountDTO.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());

            // Chuyển đổi từ Set<Role> sang List<Role>
            List<Role> rolesList = new ArrayList<>(rolesSet);
            account.setRoles(rolesList);
        }

        // Lưu tài khoản đã cập nhật
        Account savedAccount = accountRepository.save(account);
        return accountMapper.accountToAccountDTO(savedAccount);
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
        // Bạn có thể sử dụng JavaMailSender hoặc tích hợp với các dịch vụ gửi email bên
        // ngoài như SendGrid, Mailgun
        System.out.println("Gửi email đến: " + email + " với đường dẫn đặt lại mật khẩu: " + resetPasswordLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Account account = accountRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset password token."));

        account.setPassword(passwordEncoder.encode(newPassword));
        account.setResetPasswordToken(null); // Xóa token sau khi đặt lại mật khẩu
        accountRepository.save(account);

        // Gửi thông báo email cho người dùng về việc thay đổi mật khẩu thành công (tùy
        // chọn)
        sendPasswordResetConfirmationEmail(account.getEmail());
    }

    private void sendPasswordResetConfirmationEmail(String email) {
        // Implement việc gửi email xác nhận thay đổi mật khẩu
        System.out.println("Gửi email xác nhận thay đổi mật khẩu đến: " + email);
    }

    @Transactional
    public AccountDTO updateAccount(Long accountId, AccountDTO accountDTO) {
        // Tìm tài khoản theo ID
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));

        // Cập nhật các trường cần thiết
        existingAccount.setUsername(accountDTO.getUsername());
        existingAccount.setEmail(accountDTO.getEmail());
        existingAccount.setEnable(accountDTO.isEnable());
        existingAccount.setVerifiedEmail(accountDTO.isVerifiedEmail());
        existingAccount.setAuthenticationType(AuthenticationType.valueOf(accountDTO.getAuthenticationType()));
        existingAccount.setStatus(EAccountStatus.valueOf(accountDTO.getStatus()));
        existingAccount.setVersion(accountDTO.getVersion());

        // Cập nhật roles nếu có
        if (accountDTO.getRoles() != null && !accountDTO.getRoles().isEmpty()) {
            Set<Role> rolesSet = accountDTO.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());

            // Chuyển đổi từ Set<Role> sang List<Role>
            List<Role> rolesList = new ArrayList<>(rolesSet);
            existingAccount.setRoles(rolesList);
        }

        // Nếu có cập nhật password, mã hóa mật khẩu mới
        if (accountDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
            existingAccount.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        }

        // Lưu tài khoản đã cập nhật
        Account savedAccount = accountRepository.save(existingAccount);

        // Chuyển đổi Entity thành DTO để trả về
        return accountMapper.accountToAccountDTO(savedAccount);
    }

}
