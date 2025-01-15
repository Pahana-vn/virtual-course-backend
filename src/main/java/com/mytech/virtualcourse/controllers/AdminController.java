package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-instructors")
    public ResponseEntity<List<AccountDTO>> getPendingInstructors() {
        List<AccountDTO> dtos = adminService.getPendingInstructors();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve-instructor/{accountId}")
    public ResponseEntity<?> approveInstructor(@PathVariable Long accountId) {
        return adminService.approveInstructor(accountId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-status/{accountId}")
    public ResponseEntity<?> updateAccountStatus(@PathVariable Long accountId, @RequestParam EAccountStatus status) {
        return adminService.updateAccountStatus(accountId, status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/accounts-by-status")
    public ResponseEntity<List<AccountDTO>> getAccountsByStatus(@RequestParam EAccountStatus status) {
        List<AccountDTO> dtos = adminService.getAccountsByStatus(status);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/instructor/{instructorId}")
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable Long instructorId, @Valid @RequestBody InstructorDTO instructorDTO) {
        InstructorDTO updatedInstructor = adminService.updateInstructor(instructorId, instructorDTO);
        return ResponseEntity.ok(updatedInstructor);
    }
}