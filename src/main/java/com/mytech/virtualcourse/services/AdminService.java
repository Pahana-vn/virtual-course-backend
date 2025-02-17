package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.enums.EAccountStatus;
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
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    public List<AccountDTO> getPendingInstructors() {
        List<Account> pendingInstructors = accountRepository.findAll().stream()
                .filter(a -> a.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("INSTRUCTOR")) && a.getStatus() == EAccountStatus.PENDING)
                .collect(Collectors.toList());

        return pendingInstructors.stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> approveInstructor(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        boolean isInstructor = account.getRoles().stream()
                .anyMatch(r -> r.getName().equals("INSTRUCTOR"));

        if (!isInstructor) {
            return ResponseEntity.badRequest().body(new MessageDTO("Account is not an instructor."));
        }

        account.setStatus(EAccountStatus.ACTIVE);
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("Instructor account approved successfully."));
    }

    public ResponseEntity<?> updateAccountStatus(Long accountId, EAccountStatus newStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));


        account.setStatus(newStatus);
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("Account status updated successfully to " + newStatus));
    }

    public List<AccountDTO> getAccountsByStatus(EAccountStatus status) {
        List<Account> accounts = accountRepository.findAll().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());

        return accounts.stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }
}
