package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LectureDTO {
    private Long id; // ID của bài giảng
    private String titleLecture; // Tiêu đề bài giảng // Tài nguyên đính kèm
    private Integer lectureOrder; // Thứ tự của bài giảng
    private String lectureVideo;

    private Long sectionId;

    private List<ArticleDTO> articles; // Danh sách câu hỏi
}
