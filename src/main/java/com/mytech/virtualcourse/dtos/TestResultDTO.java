package com.mytech.virtualcourse.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestResultDTO {
    private Long testId;
    private String testTitle;
    private String studentLastname;
    private String studentFirstname;
    private Integer marksObtained;
    private Double percentage;
    private Boolean passed;
}