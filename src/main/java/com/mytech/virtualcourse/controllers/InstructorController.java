package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.security.SecurityUtils;
import com.mytech.virtualcourse.services.InstructorService;
import com.mytech.virtualcourse.services.QuestionService;
import com.mytech.virtualcourse.services.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private TestService testService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<InstructorDTO>> getAllInstructors(@RequestParam(required = false) String platform) {
        List<InstructorDTO> instructors = instructorService.getAllInstructors(platform);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable Long id) {
        InstructorDTO instructor = instructorService.getInstructorById(id);
        return ResponseEntity.ok(instructor);
    }

    @PostMapping
    public ResponseEntity<InstructorDTO> registerInstructor(@RequestBody InstructorDTO  instructorDTO ) {
        InstructorDTO createdInstructor = instructorService.createInstructor(instructorDTO);
        return new ResponseEntity<>(createdInstructor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable Long id, @RequestBody InstructorDTO instructorDTO) {
        InstructorDTO updatedInstructor = instructorService.updateInstructor(id, instructorDTO);
        return ResponseEntity.ok(updatedInstructor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/courses/{courseId}/tests")
    public ResponseEntity<List<TestDTO>> getCourseTestsById(@PathVariable Long id, @PathVariable Long courseId) {
        List<TestDTO> tests = testService.getTestsByInstructorIdAndCourseId(id, courseId);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/{id}/instructor-details")
    public ResponseEntity<InstructorDetailsDTO> getInstructorDetails(@PathVariable Long id) {
        InstructorDetailsDTO instructorDetails = instructorService.getInstructorDetails(id);
        return ResponseEntity.ok(instructorDetails);
    }

    @GetMapping("/{id}/instructor-statistics")
    public ResponseEntity<InstructorStatisticsDTO> getInstructorStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getInstructorStatistics(id));
    }

    @GetMapping("/{id}/instructor-profile")
    public ResponseEntity<InstructorProfileDTO> getInstructorProfile(@PathVariable Long id) {
        InstructorProfileDTO profileDTO = instructorService.getProfileByInstructorId(id);
        return ResponseEntity.ok(profileDTO);
    }

    @PutMapping("/{id}/instructor-profile")
    public ResponseEntity<InstructorProfileDTO> updateInstructorProfile(@PathVariable Long id, @RequestBody InstructorProfileDTO profileDTO) {
        InstructorProfileDTO updatedProfile = instructorService.updateProfileByInstructorId(id, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    // Cập nhật thông tin hồ sơ của giảng viên đã đăng nhập
    @PutMapping("/instructor-profile")
    public ResponseEntity<InstructorProfileDTO> updateLoggedInInstructorProfile(HttpServletRequest request, @RequestBody InstructorProfileDTO profileDTO) {
        Long accountId = securityUtils.getLoggedInAccountId();
        InstructorProfileDTO updatedProfile = instructorService.updateProfileByLoggedInInstructor(request, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePasswordStudent(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Long instructorId = SecurityUtils.getLoggedInInstructorId();
        try {
            instructorService.changePassword(instructorId, changePasswordDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    // InstructorController.java - Thêm các endpoint mới

    // Endpoint để lấy thống kê chi tiết của instructor
    @GetMapping("/{instructorId}/statistics")
    public ResponseEntity<Map<String, Object>> getInstructorStatistics(
            @PathVariable Long instructorId,
            @RequestParam(defaultValue = "month") String timeRange) {
        Map<String, Object> statistics = instructorService.getDetailedStatistics(instructorId, timeRange);
        return ResponseEntity.ok(statistics);
    }

    // Endpoint để lấy metrics hiệu suất của instructor
    @GetMapping("/{instructorId}/performance-metrics")
    public ResponseEntity<Map<String, Object>> getInstructorPerformanceMetrics(
            @PathVariable Long instructorId) {
        Map<String, Object> metrics = instructorService.getPerformanceMetrics(instructorId);
        return ResponseEntity.ok(metrics);
    }

    // Endpoint để lấy danh sách khóa học của instructor
    @GetMapping("/{instructorId}/courses")
    public ResponseEntity<Page<CourseDTO>> getInstructorCourses(
            @PathVariable Long instructorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseDTO> courses = instructorService.getInstructorCourses(instructorId, status, page, size);
        return ResponseEntity.ok(courses);
    }

    // Endpoint để lấy danh sách bài kiểm tra của instructor
    @GetMapping("/{instructorId}/tests")
    public ResponseEntity<Page<TestDTO>> getInstructorTests(
            @PathVariable Long instructorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TestDTO> tests = instructorService.getInstructorTests(instructorId, status, page, size);
        return ResponseEntity.ok(tests);
    }

    // Endpoint để lấy danh sách đánh giá của instructor
    @GetMapping("/{instructorId}/reviews")
    public ResponseEntity<Page<ReviewDTO>> getInstructorReviews(
            @PathVariable Long instructorId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewDTO> reviews = instructorService.getInstructorReviews(instructorId, rating, page, size);
        return ResponseEntity.ok(reviews);
    }

    // Endpoint để lấy tài liệu của instructor
    @GetMapping("/{instructorId}/documents")
    public ResponseEntity<List<DocumentDTO>> getInstructorDocuments(
            @PathVariable Long instructorId) {
        List<DocumentDTO> documents = instructorService.getInstructorDocuments(instructorId);
        return ResponseEntity.ok(documents);
    }

    // Endpoint để tải lên tài liệu của instructor
    @PostMapping("/{instructorId}/documents")
    public ResponseEntity<DocumentDTO> uploadInstructorDocument(
            @PathVariable Long instructorId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String documentType) {
        DocumentDTO document = instructorService.uploadInstructorDocument(instructorId, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping("/{id}/account-id")
    public ResponseEntity<Long> getInstructorAccountId(@PathVariable Long id) {
        Long accountId = instructorService.getInstructorAccountId(id);
        return ResponseEntity.ok(accountId);
    }

}
