package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.dtos.InstructorStatisticsDTO;
import com.mytech.virtualcourse.services.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @GetMapping
    public ResponseEntity<List<InstructorDTO>> getAllInstructors() {
        List<InstructorDTO> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable Long id) {
        InstructorDTO instructor = instructorService.getInstructorById(id);
        return ResponseEntity.ok(instructor);
    }

    @PostMapping
    public ResponseEntity<InstructorDTO> createInstructor(@RequestBody InstructorDTO instructorDTO) {
        InstructorDTO createdInstructor = instructorService.createInstructor(instructorDTO);
        return new ResponseEntity<>(createdInstructor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable Long id, @RequestBody InstructorDTO instructorDTO) {
        InstructorDTO updatedInstructor = instructorService.updateInstructor(id, instructorDTO);
        return ResponseEntity.ok(updatedInstructor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructor(@PathVariable Long id) {
        // Lấy danh sách các khóa học của instructor
        List<CourseDTO> courseDTOs = instructorService.getCoursesByInstructor(id);
        return ResponseEntity.ok(courseDTOs);
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<InstructorStatisticsDTO> getInstructorStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getInstructorStatistics(id));
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<Map<String, String>> getInstructorAvatar(@PathVariable Long id) {
        String avatarFileName = instructorService.getInstructorAvatar(id);

        if (avatarFileName == null || avatarFileName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Avatar not found"));
        }

        String avatarUrl = "http://localhost:8080/uploads/instructor/" + avatarFileName;

        // Trả về đối tượng JSON chứa URL
        Map<String, String> response = new HashMap<>();
        response.put("url", avatarUrl);

        return ResponseEntity.ok(response);
    }
}
