package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentQuizResultDTO {
    private Long quizId;
    private String testTitle;
    private String date;
    private int totalQuestions;
    private int totalMarks;
    private int correctAnswers;
    private int incorrectAnswers;
    private int earnedMarks;
    private boolean passed;
    private double percentage;
}
