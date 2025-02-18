package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ECourseStatus;
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

    // Endpoint từ chối giảng viên với lý do
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reject-instructor/{accountId}")
    public ResponseEntity<?> rejectInstructor(@PathVariable Long accountId, @RequestBody String rejectReason) {
        return adminService.rejectInstructor(accountId, rejectReason);
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

    // Endpoint phê duyệt khóa học
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve-course/{courseId}")
    public ResponseEntity<?> approveCourse(@PathVariable Long courseId) {
        return adminService.approveCourse(courseId);
    }

    // Endpoint từ chối khóa học với lý do
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reject-course/{courseId}")
    public ResponseEntity<?> rejectCourse(@PathVariable Long courseId, @RequestBody String rejectReason) {
        return adminService.rejectCourse(courseId, rejectReason);
    }

    // Endpoint lấy danh sách khóa học theo trạng thái
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/courses-by-status")
    public ResponseEntity<List<CourseDTO>> getCoursesByStatus(@RequestParam ECourseStatus status) {
        List<CourseDTO> dtos = adminService.getCoursesByStatus(status);
        return ResponseEntity.ok(dtos);
    }
}
