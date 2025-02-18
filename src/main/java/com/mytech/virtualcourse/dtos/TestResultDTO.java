package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestResultDTO {
    private Long testId;
    private Long studentId;
    private Integer marksObtained;
    private Double percentage;
    private Boolean passed;
    private List<StudentQuestionDTO> questions;
}