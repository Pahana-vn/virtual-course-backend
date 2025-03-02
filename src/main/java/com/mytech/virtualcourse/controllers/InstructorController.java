package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.security.SecurityUtils;
import com.mytech.virtualcourse.services.InstructorService;
import com.mytech.virtualcourse.services.QuestionService;
import com.mytech.virtualcourse.services.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private TestService testService;

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
}
