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
        return ResponseEntity.badRequest().body("Course is not in PENDING_APPROVAL status.");
        // set status = APPROVED
    }

    @PutMapping("/{courseId}/reject")
    public ResponseEntity<?> rejectCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> request
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return ResponseEntity.badRequest().body("Course is not in PENDING_APPROVAL status.");

    }
}