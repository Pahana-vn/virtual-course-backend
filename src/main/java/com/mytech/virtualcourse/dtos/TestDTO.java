package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.enums.StatusTest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestDTO {
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    private Integer totalMarks;

    @NotNull(message = "Pass percentage is required")
    @Min(value = 1, message = "Pass percentage must be at least 1")
    private Integer passPercentage;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    private Boolean isFinalTest;

    private StatusTest statusTest;

    private Long courseId;

    private Long instructorId;

    private List<QuestionDTO> questions;
}
