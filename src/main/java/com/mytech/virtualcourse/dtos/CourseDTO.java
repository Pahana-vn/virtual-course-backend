package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.enums.ECourseLevel;
import com.mytech.virtualcourse.enums.ECourseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private Long categoryId;
    private String categoryName;
    private ECourseLevel level;
    private String imageCover;
    private String urlVideo;
    private String hashtag;
    private Integer duration;
    private BigDecimal basePrice;
    private ECourseStatus status;
    private Integer progress;
    private String instructorPhoto;
    private String instructorFirstName;
    private String instructorLastName;
    private Long instructorId;
    private InstructorInfo instructorInfo;
    private List<SectionDTO> sections;
    private List<QuestionDTO> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}