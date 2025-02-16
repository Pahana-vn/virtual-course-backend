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
public class LectureDTO {
    private Long id;
    private String titleLecture;
    private String lectureVideo;
    private String lectureResource;
    private Integer lectureOrder;
    private boolean completed;
    private List<ArticleDTO> articles;
}
