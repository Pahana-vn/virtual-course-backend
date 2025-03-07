package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.dtos.QuestionImportDTO;
import com.mytech.virtualcourse.entities.Question;
import com.mytech.virtualcourse.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByCourse(@PathVariable Long courseId) {
        List<QuestionDTO> questionDTOs = questionService.getQuestionsByCourse(courseId);
        return ResponseEntity.ok(questionDTOs);
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByTest(@PathVariable Long testId) {
        List<QuestionDTO> questionDTOs = questionService.getQuestionsByTest(testId);
        return ResponseEntity.ok(questionDTOs);
    }

    @PostMapping("/import")
    public ResponseEntity<List<QuestionDTO>> importQuestions(@RequestBody List<QuestionDTO> questionDTOs) {
        return ResponseEntity.ok(questionDTOs);
    }

    @GetMapping("/export/{courseId}")
    public ResponseEntity<List<QuestionDTO>> exportQuestions(@PathVariable Long courseId) {
        List<QuestionDTO> exportedQuestions = questionService.exportQuestions(courseId);
        return ResponseEntity.ok(exportedQuestions);
    }
}
