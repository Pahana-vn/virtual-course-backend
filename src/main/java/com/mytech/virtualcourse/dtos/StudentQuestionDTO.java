package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentQuestionDTO {
    private Long id;
    private String content;
    private QuestionType type;
    private Integer marks;
    private List<AnswerOptionDTO> givenAnswers;
    private List<AnswerOptionDTO> correctAnswers;
    private boolean isCorrect;
}
