// src/main/java/com/mytech/virtualcourse/services/AdminService.java
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ERole;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.AccountMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    /**
     * Lấy danh sách các tài khoản Instructor đang chờ duyệt.
     *
     * @return Danh sách AccountDTO.
     */
    public List<AccountDTO> getPendingInstructors() {
        List<Account> pendingInstructors = accountRepository.findByRoleAndStatus(ERole.INSTRUCTOR, EAccountStatus.PENDING);

        return pendingInstructors.stream()
                .map(accountMapper::toAccount)
                .collect(Collectors.toList());
    }

    /**
     * Duyệt một tài khoản Instructor.
     *
     * @param accountId ID của tài khoản cần duyệt.
     * @return Phản hồi về kết quả duyệt.
     */
    public ResponseEntity<?> approveInstructor(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Kiểm tra xem tài khoản có vai trò INSTRUCTOR không
        boolean isInstructor = account.getRoles().stream()
                .anyMatch(r -> r.getName().equals(ERole.INSTRUCTOR.name()));

        if (!isInstructor) {
            return ResponseEntity.badRequest().body(new MessageDTO("Account is not an instructor."));
        }

        // Kiểm tra trạng thái hiện tại
        if (account.getStatus() != EAccountStatus.PENDING) {
            return ResponseEntity.badRequest().body(new MessageDTO("Account is not pending approval."));
        }

        // Cập nhật trạng thái thành ACTIVE
        account.setStatus(EAccountStatus.ACTIVE);
        accountRepository.save(account);

        // Có thể thêm các bước bổ sung khác nếu cần, ví dụ: gửi email thông báo cho người dùng

        return ResponseEntity.ok(new MessageDTO("Instructor account approved successfully."));
    }

    /**
     * Cập nhật trạng thái của tài khoản.
     *
     * @param accountId ID của tài khoản.
     * @param newStatus Trạng thái mới.
     * @return Phản hồi về kết quả cập nhật.
     */
    public ResponseEntity<?> updateAccountStatus(Long accountId, EAccountStatus newStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setStatus(newStatus);
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("Account status updated successfully to " + newStatus));
    }

    /**
     * Lấy danh sách các tài khoản theo trạng thái.
     *
     * @param status Trạng thái cần lọc.
     * @return Danh sách AccountDTO.
     */
    public List<AccountDTO> getAccountsByStatus(EAccountStatus status) {
        List<Account> accounts = accountRepository.findAll().stream()
                .filter(a -> a.getStatus() == status)
                .toList();

        return accounts.stream()
                .map(accountMapper::toAccount)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin của một Instructor.
     *
     * @param instructorId ID của Instructor.
     * @param instructorDTO Dữ liệu cập nhật.
     * @return InstructorDTO đã được cập nhật.
     */
    public InstructorDTO updateInstructor(Long instructorId, InstructorDTO instructorDTO) {
        return instructorService.updateInstructor(instructorId, instructorDTO);
    }
}
