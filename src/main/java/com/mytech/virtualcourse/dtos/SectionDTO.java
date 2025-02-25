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
    private Long id;
    private String titleSection;
    private Integer numOfLectures;
    private Integer sessionDuration;
    private Integer sequenceNumber;

    private Long courseId;

    private List<LectureDTO> lectures;
}
