package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.entities.Question;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.enums.CourseLevel;
import com.mytech.virtualcourse.enums.EStatusCourse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CourseDTO {

//    private Long id; // ID của khóa học
    private String titleCourse; // Tên khóa học
    private String description; // Mô tả khóa học
    private Long categoryId;  // ID của danh mục
    private String categoryName; // Tên danh mục
    private CourseLevel level; // Mức độ khóa học (BEGINNER, INTERMEDIATE, ADVANCED)
    private String imageCover; // Ảnh bìa
    private String urlVideo; // URL video giới thiệu
    private String hashtag; // Hashtag của khóa học
    private Integer duration; // Tổng thời lượng (phút)
    private BigDecimal basePrice; // Giá cơ bản
    private EStatusCourse status; // Trạng thái khóa học (ACTIVE, INACTIVE)
    private String instructorPhoto;
    private String instructorFirstName;
    private String instructorLastName;

    private Long instructorId; // ID của giảng viên
    private InstructorInfo instructorInfo;

    private List<SectionDTO> sections;

    private List<QuestionDTO> questions;



}