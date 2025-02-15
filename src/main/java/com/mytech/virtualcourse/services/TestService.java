package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentTestSubmissionRepository submissionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;


    public TestResultDTO submitTest(StudentTestSubmissionDTO submissionDTO) {
        // Lấy test
        Test test = testRepository.findById(submissionDTO.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        Student student = studentRepository.findById(submissionDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Tính điểm
        int totalMarks = test.getTotalMarks();
        int obtainedMarks = 0;
        for (QuestionAnswerDTO qa : submissionDTO.getAnswers()) {
            Question q = questionRepository.findById(qa.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
            boolean correct = checkAnswer(q, qa.getSelectedOptionIds());
            if (correct) {
                obtainedMarks += q.getMarks();
            }
        }

        double percentage = (obtainedMarks * 100.0) / totalMarks;
        boolean passed = percentage >= test.getPassPercentage();

        // Lưu submission
        StudentTestSubmission sts = new StudentTestSubmission();
        sts.setStudent(student);
        sts.setTest(test);
        sts.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
        sts.setMarksObtained(obtainedMarks);
        sts.setPassed(passed);
        sts.setDurationTest(test.getDuration());
        submissionRepository.save(sts);

        // Trả về kết quả
        TestResultDTO result = new TestResultDTO();
        result.setTestId(test.getId());
        result.setStudentId(student.getId());
        result.setMarksObtained(obtainedMarks);
        result.setPercentage(percentage);
        result.setPassed(passed);
        return result;
    }

    private boolean checkAnswer(Question q, List<Long> selectedOptionIds) {
        List<AnswerOption> correctOptions = q.getAnswerOptions().stream()
                .filter(AnswerOption::getIsCorrect)
                .collect(Collectors.toList());
        List<Long> correctOptionIds = correctOptions.stream().map(AnswerOption::getId).collect(Collectors.toList());

        // Kiểm tra nếu số đáp án chọn bằng số đáp án đúng và tất cả đều đúng
        return correctOptionIds.size() == selectedOptionIds.size() && correctOptionIds.containsAll(selectedOptionIds);
    }

    private QuestionDTO mapToQuestionDTO(Question q) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getId());
        dto.setContent(q.getContent());
        dto.setType(q.getType().toString());
        dto.setAnswerOptions(q.getAnswerOptions().stream().map(opt -> {
            AnswerOptionDTO aodto = new AnswerOptionDTO();
            aodto.setId(opt.getId());
            aodto.setContent(opt.getContent());
            return aodto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
