package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.entities.Question;
import com.mytech.virtualcourse.mappers.QuestionMapper;
import com.mytech.virtualcourse.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper; // Sử dụng mapper ở tầng service

    public List<QuestionDTO> getQuestionsByCourse(Long courseId) {
        // Lấy danh sách Question từ database
        List<Question> questions = questionRepository.findByCourseId(courseId);

        // Ánh xạ sang DTO trước khi trả về
        return questionMapper.toDTOList(questions);
    }
}
