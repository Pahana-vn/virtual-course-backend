package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "favorite_course",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"})
)
public class FavoriteCourse extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Học viên yêu thích khóa học

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Khóa học được yêu thích
}
