package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course-approval")
public class CourseApprovalController {

    @Autowired
    private CourseRepository courseRepository;

    @PutMapping("/{courseId}/approve")
    public ResponseEntity<?> approveCourse(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!"PENDING_APPROVAL".equals(course.getStatus())) {
            return ResponseEntity.badRequest().body("Course is not in PENDING_APPROVAL status.");
        }
        // set status = APPROVED
        course.setStatus("APPROVED");
        courseRepository.save(course);

        // => Optionally, gửi notification đến Instructor
        return ResponseEntity.ok("Course approved successfully");
    }

    @PutMapping("/{courseId}/reject")
    public ResponseEntity<?> rejectCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> request
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!"PENDING_APPROVAL".equals(course.getStatus())) {
            return ResponseEntity.badRequest().body("Course is not in PENDING_APPROVAL status.");
        }

        String reason = request.get("reason"); // Lý do reject
        // Lưu reason vào 1 field, ví dụ course.setRejectReason(...)
        // Hoặc tạo 1 bảng logs

        course.setStatus("REJECTED");
        courseRepository.save(course);

        // => Optionally, gửi notification đến Instructor
        return ResponseEntity.ok("Course rejected with reason: " + reason);
    }
}