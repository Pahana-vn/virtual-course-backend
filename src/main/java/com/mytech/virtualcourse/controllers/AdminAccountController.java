// src/main/java/com/mytech/virtualcourse/controllers/AccountController.java
package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.enums.ERole;
import com.mytech.virtualcourse.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AdminAccountController {

    private final AccountService accountService;

    public AdminAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Lấy thông tin Account theo ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        AccountDTO account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    /**
     * Lấy danh sách tất cả các Account.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Tạo một Account mới.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        AccountDTO createdAccount = accountService.createAccount(accountDTO);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    /**
     * Thêm Instructor vào một Account đã tồn tại.
     */
    @PostMapping("/{accountId}/instructor")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<InstructorDTO> createInstructorForAccount(@PathVariable Long accountId, @RequestBody InstructorDTO instructorDTO) {
        InstructorDTO createdInstructor = accountService.addInstructorToAccount(accountId, instructorDTO);
        return new ResponseEntity<>(createdInstructor, HttpStatus.CREATED);
    }

    /**
     * Thêm Student vào một Account đã tồn tại.
     */
    @PostMapping("/{accountId}/student")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<StudentDTO> createStudentForAccount(@PathVariable Long accountId, @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = accountService.addStudentToAccount(accountId, studentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    /**
     * Xóa Account theo ID.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vô hiệu hóa Account.
     */
    @PutMapping("/{accountId}/disable")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<String> disableAccount(@PathVariable Long accountId) {
        accountService.disableAccount(accountId);
        return ResponseEntity.ok("Account disabled successfully");
    }

    /**
     * Kích hoạt Account.
     */
    @PutMapping("/{accountId}/enable")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<String> enableAccount(@PathVariable Long accountId) {
        accountService.enableAccount(accountId);
        return ResponseEntity.ok("Account enabled successfully");
    }

    /**
     * Gán một vai trò cho Account.
     */
    @PostMapping("/{accountId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<String> assignRoleToAccount(@PathVariable Long accountId, @PathVariable ERole roleName) {
        accountService.assignRoleToAccount(accountId, roleName);
        return ResponseEntity.ok("Role assigned successfully");
    }

    /**
     * Loại bỏ một vai trò khỏi Account.
     */
    @DeleteMapping("/{accountId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền truy cập
    public ResponseEntity<String> removeRoleFromAccount(@PathVariable Long accountId, @PathVariable ERole roleName) {
        accountService.removeRoleFromAccount(accountId, roleName);
        return ResponseEntity.ok("Role removed successfully");
    }

    /**
     * Chỉnh sửa một tài khoản dựa trên accountId.
     *
     * @param accountId  ID của tài khoản cần chỉnh sửa.
     * @param accountDTO Dữ liệu cập nhật của tài khoản.
     * @return Phản hồi chứa dữ liệu tài khoản đã được cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable("id") Long accountId,
            @Valid @RequestBody AccountDTO accountDTO) {
        AccountDTO updatedAccount = accountService.updateAccount(accountId, accountDTO);
        return ResponseEntity.ok(updatedAccount);
    }
}
