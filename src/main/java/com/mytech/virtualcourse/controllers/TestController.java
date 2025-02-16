package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.TestDTO;
import com.mytech.virtualcourse.security.SecurityUtils;
import com.mytech.virtualcourse.services.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    private final SecurityUtils securityUtils;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<TestDTO>> getTestsByCourse(@PathVariable Long courseId) {
        List<TestDTO> tests = testService.getTestsByCourse(courseId);
        return ResponseEntity.ok(tests);
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<TestDTO> createTest(@PathVariable Long courseId, @Valid @RequestBody TestDTO testDTO) {
        Long loggedInInstructorId  = securityUtils.getLoggedInInstructorId();
        TestDTO createdTest = testService.createTestForCourse(courseId, testDTO, loggedInInstructorId );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestDTO> updateTest(@PathVariable Long id, @Valid @RequestBody TestDTO testDTO) {
        Long loggedInInstructorId = securityUtils.getLoggedInInstructorId();
        TestDTO updatedTest = testService.updateTest(id, testDTO, loggedInInstructorId);
        return ResponseEntity.ok(updatedTest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        Long loggedInInstructorId = securityUtils.getLoggedInInstructorId();
        testService.deleteTest(id, loggedInInstructorId);
        return ResponseEntity.noContent().build();
    }

}
