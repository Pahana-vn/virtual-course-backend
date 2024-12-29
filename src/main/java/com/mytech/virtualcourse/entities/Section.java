package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "section")
public class Section extends AbstractEntity {

    @Column(name = "title_section", nullable = false)
    private String titleSection;

    @Column(name = "num_of_lectures", nullable = false)
    private Integer numOfLectures;

    @Column(name = "num_of_quiz")
    private Integer numOfQuiz;

    @Column(name = "num_of_test")
    private Integer numOfTest;

    @Column(name = "session_duration", nullable = false)
    private Integer sessionDuration;

    @Column(name = "pass_score")
    private Integer passScore;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber; // Số thứ tự của phần học trong khóa học

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures;

//    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Assignment> assignments; // Bài tập thuộc phần học

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningProgress> progress; // Tiến trình học từng phần
}
