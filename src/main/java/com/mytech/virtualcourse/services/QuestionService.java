package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.entities.Question;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.QuestionMapper;
import com.mytech.virtualcourse.repositories.QuestionRepository;
import com.mytech.virtualcourse.repositories.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final QuestionMapper questionMapper; // Sử dụng mapper ở tầng service

    public List<QuestionDTO> getQuestionsByCourse(Long courseId) {
        // Lấy danh sách Question từ database
        List<Question> questions = questionRepository.findByCourseId(courseId);

        // Ánh xạ sang DTO trước khi trả về
        return questionMapper.toDTOList(questions);
    }

    public List<QuestionDTO> getQuestionsByTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with ID: " + testId));

        List<Question> questions = test.getQuestions();
        return questions.stream()
                .map(questionMapper::questionToQuestionDTO)
                .collect(Collectors.toList());
    }
}
