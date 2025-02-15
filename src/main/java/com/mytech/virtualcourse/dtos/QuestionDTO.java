package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionDTO {
    private Long id;
    private String content;
    private String type; // "SINGLE" hoặc "MULTIPLE"
    private Integer marks;
    private Long courseId;
    private List<AnswerOptionDTO> answerOptions;
}
