package com.mytech.virtualcourse.dtos;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SectionDTO {
    private Long id;
    private String titleSection;
    private Integer numOfLectures;
    private Integer numOfQuiz;
    private Integer numOfTest;
    private Integer sessionDuration;
    private Integer passScore;
    private Integer sequenceNumber;
    private List<LectureDTO> lectures;
}
