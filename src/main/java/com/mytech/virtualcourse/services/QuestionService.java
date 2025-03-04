package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.AnswerOptionDTO;
import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.dtos.QuestionImportDTO;
import com.mytech.virtualcourse.entities.AnswerOption;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Question;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.enums.QuestionType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.QuestionMapper;
import com.mytech.virtualcourse.repositories.AnswerOptionRepository;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.QuestionRepository;
import com.mytech.virtualcourse.repositories.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final QuestionMapper questionMapper;
    private final CourseRepository courseRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public List<QuestionDTO> getQuestionsByCourse(Long courseId) {

        List<Question> questions = questionRepository.findByCourseId(courseId);

        return questionMapper.toDTOList(questions);
    }

    public List<Question> importQuestions(List<QuestionDTO> questionDTOs) {
        List<Question> questionsToSave = new ArrayList<>();

        for (QuestionDTO questionDTO : questionDTOs) {
            Question question = null;

            if (questionDTO.getId() != null) {
                question = questionRepository.findById(questionDTO.getId()).orElse(null);

                // Nếu câu hỏi đã được dùng trong Test hoặc StudentAnswer, bỏ qua
                if (question != null && (!question.getTests().isEmpty() || !question.getStudentAnswers().isEmpty())) {
                    continue;
                }
            }

            // Nếu không tìm thấy hoặc là câu hỏi mới, tạo mới
            if (question == null) {
                question = new Question();
            }

            question.setContent(questionDTO.getContent());
            question.setMarks(questionDTO.getMarks());

            List<AnswerOption> answerOptions = new ArrayList<>();
            int correctCount = 0; // Đếm số lượng đáp án đúng

            for (AnswerOptionDTO optionDTO : questionDTO.getAnswerOptions()) {
                AnswerOption answerOption = optionDTO.getId() != null ?
                        answerOptionRepository.findById(optionDTO.getId()).orElse(new AnswerOption()) :
                        new AnswerOption();

                answerOption.setContent(optionDTO.getContent());
                answerOption.setIsCorrect(optionDTO.getIsCorrect());
                answerOption.setQuestion(question);

                if (optionDTO.getIsCorrect()) {
                    correctCount++;
                }

                answerOptions.add(answerOption);
            }

            // Xác định loại câu hỏi dựa trên số lượng đáp án đúng
            if (correctCount == 1) {
                question.setType(QuestionType.SINGLE);
            } else if (correctCount > 1) {
                question.setType(QuestionType.MULTIPLE);
            } else {
                throw new RuntimeException("Question must have at least one correct answer!");
            }

            question.setAnswerOptions(answerOptions);
            questionsToSave.add(question);
        }

        return questionRepository.saveAll(questionsToSave);
    }



    public List<QuestionDTO> exportQuestions(Long courseId) {
        List<Question> questions = questionRepository.findByCourseId(courseId);
        return questionMapper.toDTOList(questions);
    }

    public List<QuestionDTO> getQuestionsByTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with ID: " + testId));

        List<Question> questions = test.getQuestions(); // Lấy danh sách câu hỏi từ test
        return questions.stream()
                .map(questionMapper::questionToQuestionDTO)
                .collect(Collectors.toList());
    }
}
