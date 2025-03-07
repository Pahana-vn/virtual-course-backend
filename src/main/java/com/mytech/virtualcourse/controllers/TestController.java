package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.StudentTestSubmissionDTO;
import com.mytech.virtualcourse.dtos.TestDTO;
import com.mytech.virtualcourse.dtos.TestResultDTO;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.repositories.TestRepository;
import com.mytech.virtualcourse.security.SecurityUtils;
import com.mytech.virtualcourse.services.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {
    @Autowired
    private TestService testService;

    private final SecurityUtils securityUtils;
    @Autowired
    private TestRepository testRepository;

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

    @PutMapping("/{testId}/final-test")
    public ResponseEntity<Map<String, String>> updateFinalTestStatus(
            @PathVariable Long testId, @RequestBody Map<String, Boolean> request) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Test not found"));
        }

        Test test = optionalTest.get();
        Boolean isFinalTest = request.get("isFinalTest");

        try {
            test.setIsFinalTest(isFinalTest);
            testRepository.save(test);
            return ResponseEntity.ok(Collections.singletonMap("message", "Test final status updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Failed to update final test status."));
        }
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
            e.printStackTrace();  // In ra lỗi trong logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


}
