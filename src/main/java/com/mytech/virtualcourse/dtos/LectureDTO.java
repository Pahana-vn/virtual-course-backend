package com.mytech.virtualcourse.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LectureDTO {
    private Long id;
    private String titleLecture;
    private String lectureVideo;
    private String lectureResource;
    private Integer lectureOrder;
}
