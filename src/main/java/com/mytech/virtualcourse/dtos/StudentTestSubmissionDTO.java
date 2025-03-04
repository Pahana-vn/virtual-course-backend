package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentTestSubmissionDTO {
    private Long studentId;
    private Long testId;
    private List<QuestionAnswerDTO> answers;
}
