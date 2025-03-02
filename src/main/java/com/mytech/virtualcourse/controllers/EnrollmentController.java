package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.EnrollmentDTO;
import com.mytech.virtualcourse.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentDTO> enrollStudent(@RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO enrolledStudent = enrollmentService.enrollStudent(enrollmentDTO);
        return ResponseEntity.ok(enrolledStudent);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @PutMapping("/{enrollmentId}/complete")
    public ResponseEntity<EnrollmentDTO> completeEnrollment(@PathVariable Long enrollmentId) {
        EnrollmentDTO completedEnrollment = enrollmentService.completeEnrollment(enrollmentId);
        return ResponseEntity.ok(completedEnrollment);
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }
}
