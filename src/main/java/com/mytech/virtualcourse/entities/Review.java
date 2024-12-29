package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review extends AbstractEntity {

    @Column(name = "rating", nullable = false, columnDefinition = "INTEGER CHECK (rating >= 1 AND rating <= 5)")
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor; // Liên kết đến Instructor

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
