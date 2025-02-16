package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EducationDTO {
    private Long id;
    private Long instructorId;
    private String degree;
    private String university;
    private Integer startYear;
    private Integer endYear;
    private String description;
}
