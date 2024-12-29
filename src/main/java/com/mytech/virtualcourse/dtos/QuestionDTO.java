package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.enums.QuestionType;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionDTO {
//    private Long id; // ID của câu hỏi
    private String content; // Nội dung câu hỏi
    private QuestionType type; // SINGLE hoặc MULTIPLE
    private Integer marks; // Điểm số cho câu hỏi

//    private Long courseId;

//    private Long testId;

    private List<AnswerOptionDTO> answerOptions; // Danh sách đáp án
}
