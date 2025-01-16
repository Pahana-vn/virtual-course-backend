package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.enums.StatusTest;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestDTO {
    private Long id; // ID của bài kiểm tra
    private String title; // Tên bài kiểm tra
    private String description; // Mô tả bài kiểm tra
    private Integer totalMarks; // Tổng điểm của bài kiểm tra
    private Integer passPercentage; // Tỷ lệ phần trăm để pass
    private Integer duration; // Thời lượng làm bài (phút)
    private Boolean isFinalTest; // Có phải bài kiểm tra cuối khóa hay không
    private StatusTest statusTest; // Trạng thái bài kiểm tra

    private Long courseId; // ID khóa học

    private Long instructorId; // ID giảng viên tạo bài kiểm tra

    private List<QuestionDTO> questions; // Danh sách câu hỏi thuộc bài kiểm tra
}