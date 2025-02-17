package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentQuizMapper {

    // ✅ Chuyển đổi StudentTestSubmission -> StudentQuizResultDTO
    @Mapping(source = "id", target = "quizId")
    @Mapping(source = "test.title", target = "testTitle")
    @Mapping(source = "submittedAt", target = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "test.totalMarks", target = "totalMarks")
    @Mapping(source = "marksObtained", target = "earnedMarks")
    @Mapping(source = "passed", target = "passed")
    @Mapping(target = "totalQuestions", expression = "java(submission.getTest().getQuestions().size())")
    @Mapping(target = "correctAnswers", expression = "java(getCorrectAnswerCount(submission))")
    @Mapping(target = "incorrectAnswers", expression = "java(submission.getTest().getQuestions().size() - getCorrectAnswerCount(submission))")
    @Mapping(target = "percentage", expression = "java((submission.getMarksObtained() * 100.0) / submission.getTest().getTotalMarks())")
    StudentQuizResultDTO toQuizResultDTO(StudentTestSubmission submission);

    // ✅ Chuyển đổi List<StudentTestSubmission> -> List<StudentQuizResultDTO>
    List<StudentQuizResultDTO> toQuizResultDTOList(List<StudentTestSubmission> submissions);

    // ✅ Chuyển đổi Question -> QuestionDTO
    @Mapping(source = "id", target = "id")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "answerOptions", target = "answerOptions")
    QuestionDTO toQuestionDTO(Question question);

    // ✅ Chuyển đổi List<Question> -> List<QuestionDTO>
    List<QuestionDTO> toQuestionDTOList(List<Question> questions); // ⚠️ Thêm hàm này để tránh lỗi!

    // ✅ Chuyển đổi StudentTestSubmission và List<QuestionDTO> -> StudentQuizDetailDTO
    @Mapping(source = "submission.id", target = "quizId")
    @Mapping(source = "submission.test.title", target = "testTitle")
    @Mapping(source = "submission.marksObtained", target = "earnedMarks")
    @Mapping(source = "submission.test.totalMarks", target = "totalMarks")
    @Mapping(source = "questions", target = "questions")
    StudentQuizDetailDTO toQuizDetailDTO(StudentTestSubmission submission, List<QuestionDTO> questions); // ⚠️ Thêm hàm này để tránh lỗi!

    // ✅ Hàm hỗ trợ tính số câu đúng dựa trên câu trả lời đã chọn
    default int getCorrectAnswerCount(StudentTestSubmission submission) {
        return (int) submission.getAnswers().stream().filter(ans -> ans.getSelectedOption().getIsCorrect()).count();
    }
}
