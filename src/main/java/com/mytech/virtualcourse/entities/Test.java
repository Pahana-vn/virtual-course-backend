package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.StatusTest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test")
public class Test extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "pass_percentage", nullable = false)
    private Integer passPercentage = 60;

    @Column(name = "duration", nullable = false)
    private Integer duration; // Thời lượng làm bài (phút)

    @Column(name = "is_final_test", nullable = false)
    private Boolean isFinalTest = false;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusTest statusTest;


    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToMany
    @JoinTable(
            name = "test_question_mapping",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentTestSubmission> submissions;
}
