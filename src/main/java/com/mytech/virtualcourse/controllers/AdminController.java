package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.MessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ECourseStatus;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.services.AdminService;
import com.mytech.virtualcourse.services.CourseApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CourseApprovalService courseApprovalService;

    // Course-related endpoints
    @GetMapping("/courses/pending")
    public ResponseEntity<List<CourseDTO>> getPendingCourses() {
        System.out.println("Received request for pending courses");
        try {
            List<CourseDTO> pendingCourses = adminService.getCoursesByStatus(ECourseStatus.PENDING);
            System.out.println("Found " + pendingCourses.size() + " pending courses");
            return ResponseEntity.ok(pendingCourses);
        } catch (Exception e) {
            System.err.println("Error fetching pending courses: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/courses/{courseId}/approve")
    public ResponseEntity<?> approveCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("Received request to approve course with ID: " + courseId);
        System.out.println("Request body: " + request);
        System.out.println("User details: " + (userDetails != null ? userDetails.getUsername() : "null"));

        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageDTO("Authentication required"));
            }

            Account reviewer = adminService.getAdminAccount(userDetails.getUsername());
            System.out.println("Admin reviewer: " + reviewer.getUsername() + " (ID: " + reviewer.getId() + ")");

            String notes = request.get("notes");
            System.out.println("Approval notes: " + notes);

            courseApprovalService.approveCourse(courseId, reviewer, notes);
            System.out.println("Course approval successful");

            // Return a more detailed response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Course approved successfully");
            response.put("courseId", courseId);
            response.put("status", "APPROVED");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            System.err.println("Resource not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageDTO(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error approving course: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDTO("Error approving course: " + e.getMessage()));
        }
    }

    @PostMapping("/courses/{courseId}/reject")
    public ResponseEntity<?> rejectCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Account reviewer = adminService.getAdminAccount(userDetails.getUsername());
        String rejectionReason = request.get("reason");
        courseApprovalService.rejectCourse(courseId, reviewer, rejectionReason);
        return ResponseEntity.ok(new MessageDTO("Course rejected successfully"));
    }

    @GetMapping("/courses/by-status")
    public ResponseEntity<List<CourseDTO>> getCoursesByStatus(
            @RequestParam(required = true) ECourseStatus status
    ) {
        List<CourseDTO> courses = adminService.getCoursesByStatus(status);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/{courseId}/approval-history")
    public ResponseEntity<?> getCourseApprovalHistory(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseApprovalService.getCourseApprovalHistory(courseId));
    }

    // Instructor-related endpoints
    @GetMapping("/instructors/pending")
    public ResponseEntity<List<AccountDTO>> getPendingInstructors() {
        List<AccountDTO> dtos = adminService.getPendingInstructors();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/instructors/{accountId}/approve")
    public ResponseEntity<?> approveInstructor(
            @PathVariable Long accountId,
            @RequestBody(required = false) Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String notes = request != null ? request.get("notes") : null;

            // Nếu có thông tin người dùng đăng nhập, sử dụng tài khoản đó
            if (userDetails != null) {
                Account reviewer = adminService.getAdminAccount(userDetails.getUsername());
                return adminService.approveInstructor(accountId, notes);
            } else {
                // Nếu không có thông tin người dùng, sử dụng phương thức mặc định
                return adminService.approveInstructor(accountId, notes);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Error approving instructor: " + e.getMessage()));
        }
    }

    @PostMapping("/instructors/{accountId}/reject")
    public ResponseEntity<?> rejectInstructor(
            @PathVariable Long accountId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String rejectReason = request.get("reason");

            // Nếu có thông tin người dùng đăng nhập, sử dụng tài khoản đó
            if (userDetails != null) {
                Account reviewer = adminService.getAdminAccount(userDetails.getUsername());
                return adminService.rejectInstructor(accountId, rejectReason);
            } else {
                // Nếu không có thông tin người dùng, sử dụng phương thức mặc định
                return adminService.rejectInstructor(accountId, rejectReason);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Error rejecting instructor: " + e.getMessage()));
        }
    }

    @GetMapping("/accounts/by-status")
    public ResponseEntity<List<AccountDTO>> getAccountsByStatus(
            @RequestParam(required = true) EAccountStatus status
    ) {
        List<AccountDTO> accounts = adminService.getAccountsByStatus(status);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/accounts/{accountId}/status")
    public ResponseEntity<?> updateAccountStatus(
            @PathVariable Long accountId,
            @RequestParam EAccountStatus status
    ) {
        return adminService.updateAccountStatus(accountId, status);
    }

    @GetMapping("/accounts/{accountId}/roles")
    public ResponseEntity<List<String>> getAccountRoles(@PathVariable Long accountId) {
        List<String> roles = adminService.getAccountRoles(accountId);
        return ResponseEntity.ok(roles);
    }
}