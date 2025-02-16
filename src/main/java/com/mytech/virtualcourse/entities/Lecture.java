package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lecture")
public class Lecture extends AbstractEntity {

    @Column(name = "title_lecture", nullable = false)
    private String titleLecture;

    @Column(name = "lecture_video")
    private String lectureVideo;

    @Column(name = "lecture_resource")
    private String lectureResource;

    @Column(name = "lecture_order", nullable = false)
    private Integer lectureOrder;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();

}
