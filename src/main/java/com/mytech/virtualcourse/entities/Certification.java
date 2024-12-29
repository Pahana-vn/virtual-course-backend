package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certification")
public class Certification extends AbstractEntity {

    @Column(name = "issue_date", nullable = false)
    private Timestamp issueDate;

    @Column(name = "certificate_url", nullable = false)
    private String certificateUrl;

    @Column(name = "certificate_template")
    private String certificateTemplate;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;
}
