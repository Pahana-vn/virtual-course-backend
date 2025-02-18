package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.enums.QuestionType;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionDTO {
    private Long id;
    private String content;
    private QuestionType type;
    private Integer marks;
    private Long courseId;
    private List<AnswerOptionDTO> answerOptions;
    private String givenAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}
