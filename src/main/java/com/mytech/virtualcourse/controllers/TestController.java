package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.dtos.StudentTestSubmissionDTO;
import com.mytech.virtualcourse.dtos.TestResultDTO;
import com.mytech.virtualcourse.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestController {
    @Autowired
    private TestService testService;

//    @GetMapping("/{testId}/questions")
//    public ResponseEntity<List<QuestionDTO>> getQuestions(@PathVariable Long testId) {
//        List<QuestionDTO> questions = testService.getQuestionsForTest(testId);
//        return ResponseEntity.ok(questions);
//    }

    @PostMapping("/submit")
    public ResponseEntity<TestResultDTO> submitTest(@RequestBody StudentTestSubmissionDTO submissionDTO) {
        TestResultDTO result = testService.submitTest(submissionDTO);
        return ResponseEntity.ok(result);
    }
}