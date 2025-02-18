package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.entities.Question;
import com.mytech.virtualcourse.entities.StudentTestSubmission;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentQuizDetailDTO {
    private Long quizId;
    private String testTitle;
    private int totalMarks;
    private int earnedMarks;
    private double percentage;
    private boolean passed;
    private List<StudentQuestionDTO> questions;
}
