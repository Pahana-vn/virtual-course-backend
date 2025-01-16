package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private Long id;
    private String titleCourse;
    private String description;
    private String categoryName;
    private Long categoryId;
    private String level;
    private String imageCover;
    private String urlVideo;
    private String hashtag;
    private Integer duration;
    private BigDecimal basePrice;
    private String status;
    private String instructorPhoto;
    private String instructorFirstName;
    private String instructorLastName;
    private Long instructorId; // Thêm nếu cần
    private Integer progress;//them moi tu Nhan
    private InstructorInfo instructorInfo;

    private List<SectionDTO> sections;

    private List<QuestionDTO> questions;

}