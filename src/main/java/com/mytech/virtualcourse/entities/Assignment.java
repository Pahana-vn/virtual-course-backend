package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignment")
public class Assignment extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private Timestamp dueDate;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture; // Bài giảng liên quan đến bài tập (nếu cần liên kết cụ thể)

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAssignmentSubmission> submissions; // Danh sách bài nộp

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section; // Liên kết với Section

}
