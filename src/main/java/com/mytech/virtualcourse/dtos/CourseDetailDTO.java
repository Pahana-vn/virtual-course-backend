package com.mytech.virtualcourse.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseDetailDTO {
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
    private String instructorId;
    private String instructorPhoto;
    private String instructorFirstName;
    private String instructorLastName;
    private String instructorTitle;
    private Integer progress; //Check lại
    private List<SectionDTO> sections;

    private Boolean allLecturesCompleted;
    private Long finalTestId;
    private String finalTestTitle;
}
