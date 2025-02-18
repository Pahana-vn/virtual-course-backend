package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.StudentTestSubmissionDTO;
import com.mytech.virtualcourse.dtos.TestDTO;
import com.mytech.virtualcourse.dtos.TestResultDTO;
import com.mytech.virtualcourse.security.SecurityUtils;
import com.mytech.virtualcourse.services.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {
    @Autowired
    private TestService testService;

    private final SecurityUtils securityUtils;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<TestDTO>> getTestsByCourse(@PathVariable Long courseId) {
        List<TestDTO> tests = testService.getTestsByCourse(courseId);
        return ResponseEntity.ok(tests);
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<TestDTO> createTest(@PathVariable Long courseId, @Valid @RequestBody TestDTO testDTO) {
        Long loggedInInstructorId = SecurityUtils.getLoggedInInstructorId();
        TestDTO createdTest = testService.createTestForCourse(courseId, testDTO, loggedInInstructorId );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestDTO> updateTest(@PathVariable Long id, @Valid @RequestBody TestDTO testDTO) {
        Long loggedInInstructorId = SecurityUtils.getLoggedInInstructorId();
        TestDTO updatedTest = testService.updateTest(id, testDTO, loggedInInstructorId);
        return ResponseEntity.ok(updatedTest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        Long loggedInInstructorId = SecurityUtils.getLoggedInInstructorId();
        testService.deleteTest(id, loggedInInstructorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/submit")
    public ResponseEntity<TestResultDTO> submitTest(@RequestBody StudentTestSubmissionDTO submissionDTO) {
        System.out.println("Received submission: " + submissionDTO);
        TestResultDTO result = testService.submitTest(submissionDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/result/{testId}/student/{studentId}")
    public ResponseEntity<TestResultDTO> getTestResult(
            @PathVariable Long testId,
            @PathVariable Long studentId) {
        try {
            TestResultDTO result = testService.getTestResult(testId, studentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
