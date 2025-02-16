package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.entities.Question;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnswerOptionDTO {
    private Long id; // ID của đáp án
    private String content; // Nội dung đáp án
    private Boolean isCorrect; // Đáp án đúng hay sai

    private Long questionId;

}
