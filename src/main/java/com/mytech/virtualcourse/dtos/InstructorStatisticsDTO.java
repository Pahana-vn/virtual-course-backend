package com.mytech.virtualcourse.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class InstructorStatisticsDTO {
    private Long instructorId;
    private String instructorName;
    private Long totalCourses;
    private Long totalPublishedCourses;
    private Long totalPendingCourses;
    private Long totalStudents;
    private BigDecimal balance;
    private String avatarImage;
}
