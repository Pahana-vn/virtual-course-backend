package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionAnswerDTO {
    private Long questionId;
    private List<Long> selectedOptionIds;
}