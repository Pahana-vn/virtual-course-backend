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
public class SectionDTO {
//    private Long id; // ID của phần học
    private String titleSection; // Tên phần học
    private Integer numOfLectures; // Số lượng bài giảng
    private Integer sessionDuration; // Tổng thời lượng của phần học
    private Integer sequenceNumber; // Thứ tự của phần học trong khóa học

//    private Long courseId;

    private List<LectureDTO> lectures; // Danh sách bài giảng thuộc phần học
}
