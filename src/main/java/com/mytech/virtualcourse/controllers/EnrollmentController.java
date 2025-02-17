// src/main/java/com/mytech/virtualcourse/controllers/EnrollmentController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.EnrollmentDTO;
import com.mytech.virtualcourse.services.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Enroll a student in a course.
     *
     * @param dto the enrollment data transfer object
     * @return the created EnrollmentDTO
     */
    @PostMapping
    public ResponseEntity<EnrollmentDTO> enrollStudent(@Valid @RequestBody EnrollmentDTO dto) {
        EnrollmentDTO created = enrollmentService.enrollStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Mark an enrollment as completed.
     *
     * @param enrollmentId the ID of the enrollment
     * @return the updated EnrollmentDTO
     */
    @PutMapping("/{enrollmentId}/complete")
    public ResponseEntity<EnrollmentDTO> completeEnrollment(@PathVariable Long enrollmentId) {
        EnrollmentDTO updated = enrollmentService.completeEnrollment(enrollmentId);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    /**
     * Get all enrollments for a student.
     *
     * @param studentId the ID of the student
     * @return list of EnrollmentDTOs
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.status(HttpStatus.OK).body(enrollments);
    }

    /**
     * Get all enrollments for a course.
     *
     * @param courseId the ID of the course
     * @return list of EnrollmentDTOs
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(enrollments);
    }

    /**
     * Delete an enrollment by ID.
     *
     * @param enrollmentId the ID of the enrollment to delete
     * @return no content
     */
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
