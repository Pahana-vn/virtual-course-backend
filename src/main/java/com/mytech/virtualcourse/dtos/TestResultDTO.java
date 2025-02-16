package com.mytech.virtualcourse.dtos;

import lombok.*;

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
}