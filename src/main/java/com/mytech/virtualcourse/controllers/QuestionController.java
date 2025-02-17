package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
