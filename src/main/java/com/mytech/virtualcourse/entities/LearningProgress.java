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
@Table(name = "learning_progress")
public class LearningProgress extends AbstractEntity {

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage;

    @Column(name = "last_accessed")
    private Timestamp lastAccessed;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToOne
    @JoinColumn(name = "final_test_submission_id")
    private StudentTestSubmission finalTestSubmission;

    @Column(name = "estimated_completion_date")
    private Timestamp estimatedCompletionDate;

    @Column(name = "reminders_sent", nullable = false)
    private Integer remindersSent = 0;

    @OneToMany(mappedBy = "learningProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudySession> studySessions;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;
}