package com.mytech.virtualcourse.dtos;

import lombok.*;
import java.util.List;

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
    private List<ArticleDTO> articles;
}
