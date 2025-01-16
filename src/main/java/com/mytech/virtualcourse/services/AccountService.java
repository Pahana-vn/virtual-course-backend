// src/main/java/com/mytech/virtualcourse/services/AccountService.java
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ERole;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.AccountMapper;
import com.mytech.virtualcourse.mappers.InstructorMapper;
import com.mytech.virtualcourse.mappers.StudentMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.RoleRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import thêm nếu cần

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private InstructorMapper instructorMapper;

    /**
     * Lấy thông tin Account theo ID.
     *
     * @param id ID của tài khoản.
     * @return AccountDTO.
     */
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return accountMapper.toAccount(account);
    }

    /**
     * Lấy danh sách tất cả các Account.
     *
     * @return Danh sách AccountDTO.
     */
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toAccount)
                .collect(Collectors.toList());
    }

    /**
     * Xóa Account theo ID.
     *
     * @param id ID của tài khoản cần xóa.
     */
    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    /**
     * Vô hiệu hóa Account.
     *
     * @param accountId ID của tài khoản.
     */
    @Transactional
    public void disableAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(EAccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    /**
     * Kích hoạt Account.
     *
     * @param accountId ID của tài khoản.
     */
    @Transactional
    public void enableAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(EAccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    /**
     * Gán một vai trò cho Account.
     *
     * @param accountId ID của tài khoản.
     * @param roleName Tên vai trò.
     */
    @Transactional
    public void assignRoleToAccount(Long accountId, ERole roleName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        Role role = roleRepository.findByName(String.valueOf(roleName))
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
     *
     * @param accountId ID của tài khoản.
     * @param roleName Tên vai trò.
     */
    @Transactional
    public void removeRoleFromAccount(Long accountId, ERole roleName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        Role role = roleRepository.findByName(String.valueOf(roleName))
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
                studentRepository.delete(account.getStudent());
                account.setStudent(null);
                accountRepository.save(account);
            }
        } else {
            throw new RuntimeException("Account does not have the role: " + roleName);
        }
    }

    /**
     * Cập nhật tài khoản.
     *
     * @param accountId  ID của tài khoản cần cập nhật.
//     * @param accountDTO Dữ liệu cập nhật.
     * @return AccountDTO đã được cập nhật.
     */
    @Transactional
    public AccountDTO updateAccount(Long accountId, UpdateAccountDTO updateAccountDTO) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));

        // Cập nhật các trường thông tin
        existingAccount.setUsername(updateAccountDTO.getUsername());
        existingAccount.setEmail(updateAccountDTO.getEmail());
        existingAccount.setStatus(updateAccountDTO.getStatus());

        // Nếu có cập nhật mật khẩu, mã hóa mật khẩu mới
        if (updateAccountDTO.getPassword() != null && !updateAccountDTO.getPassword().isEmpty()) {
            existingAccount.setPassword(passwordEncoder.encode(updateAccountDTO.getPassword()));
        }

        // Cập nhật các trường khác nếu cần thiết
        existingAccount.setVerifiedEmail(updateAccountDTO.isVerifiedEmail());
        existingAccount.setAuthenticationType(updateAccountDTO.getAuthenticationType());
        existingAccount.setVersion(updateAccountDTO.getVersion());

        // Cập nhật roles nếu có
        if (updateAccountDTO.getRoles() != null && !updateAccountDTO.getRoles().isEmpty()) {

            // Chuyển đổi từ Set<Role> sang List<Role>
            List<Role> rolesList = updateAccountDTO.getRoles().stream()
                    .map(roleDTO -> roleRepository.findByName(String.valueOf(ERole.valueOf(String.valueOf(roleDTO))))
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleDTO.name()))).distinct().collect(Collectors.toList());
            existingAccount.setRoles(rolesList);
        }

        // Lưu tài khoản đã cập nhật
        Account savedAccount = accountRepository.save(existingAccount);
        return accountMapper.toAccount(savedAccount);
    }
    /**
     * Thêm Instructor vào một Account đã tồn tại, đảm bảo Account có ROLE_INSTRUCTOR.
     *
     * @param accountId ID của tài khoản.
     * @param instructorDTO Dữ liệu Instructor.
     * @return InstructorDTO đã được tạo.
     */
    @Transactional
    public InstructorDTO addInstructorToAccount(Long accountId, InstructorDTO instructorDTO) {
        // Kiểm tra xem Account có ROLE_INSTRUCTOR không
        boolean hasInstructorRole = accountRepository.hasRole(accountId, ERole.INSTRUCTOR);
        if (!hasInstructorRole) {
            throw new RuntimeException("Account does not have the INSTRUCTOR role.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Kiểm tra xem Account đã có Instructor chưa
        if (account.getInstructor() != null) {
            throw new RuntimeException("Account already has an Instructor.");
        }

        // Tạo Instructor mới từ DTO
        InstructorDTO newInstructorDTO = new InstructorDTO();
        newInstructorDTO.setAccountId(accountId);
        newInstructorDTO.setFirstName(instructorDTO.getFirstName());
        newInstructorDTO.setLastName(instructorDTO.getLastName());
        newInstructorDTO.setGender(instructorDTO.getGender());
        newInstructorDTO.setAddress(instructorDTO.getAddress());
        newInstructorDTO.setPhone(instructorDTO.getPhone());
        newInstructorDTO.setBio(instructorDTO.getBio());
        newInstructorDTO.setPhoto(instructorDTO.getPhoto());
        newInstructorDTO.setTitle(instructorDTO.getTitle());
        newInstructorDTO.setWorkplace(instructorDTO.getWorkplace());

        // Tạo Instructor thông qua InstructorService

        return instructorService.createInstructor(newInstructorDTO);
    }

    /**
     * Thêm Student vào một Account đã tồn tại, đảm bảo Account có ROLE_STUDENT.
     *
     * @param accountId ID của tài khoản.
     * @param studentDTO Dữ liệu Student.
     * @return StudentDTO đã được tạo.
     */
    @Transactional
    public StudentDTO addStudentToAccount(Long accountId, StudentDTO studentDTO) {
        // Kiểm tra xem Account có ROLE_STUDENT không
        boolean hasStudentRole = accountRepository.hasRole(accountId, ERole.STUDENT);
        if (!hasStudentRole) {
            throw new RuntimeException("Account does not have the ROLE_STUDENT role.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Kiểm tra xem Account đã có Student chưa
        if (account.getStudent() != null) {
            throw new RuntimeException("Account already has a Student.");
        }

        // Tạo Student mới từ DTO
        StudentDTO newStudentDTO = new StudentDTO();
        newStudentDTO.setAccountId(accountId);
        newStudentDTO.setFirstName(studentDTO.getFirstName());
        newStudentDTO.setLastName(studentDTO.getLastName());
        newStudentDTO.setDob(studentDTO.getDob());
        newStudentDTO.setGender(studentDTO.getGender());
        newStudentDTO.setAddress(studentDTO.getAddress());
        newStudentDTO.setPhone(studentDTO.getPhone());
        newStudentDTO.setAvatar(studentDTO.getAvatar());
        newStudentDTO.setVerifiedPhone(studentDTO.isVerifiedPhone());
        newStudentDTO.setCategoryPrefer(studentDTO.getCategoryPrefer());
        newStudentDTO.setStatusStudent(studentDTO.getStatusStudent());

        // Tạo Student thông qua StudentService

        return studentService.createStudent(newStudentDTO);
    }
}
