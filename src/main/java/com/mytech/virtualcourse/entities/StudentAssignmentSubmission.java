package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_assignment_submission")
public class StudentAssignmentSubmission extends AbstractEntity {

    @Column(name = "submission_url", nullable = false)
    private String submissionUrl; // Link bài nộp

    @Column(name = "submitted_at", nullable = false)
    private Timestamp submittedAt; // Thời gian nộp bài

    @Column(name = "marks_obtained")
    private Integer marksObtained; // Điểm đạt được

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment; // Bài tập mà học viên đã nộp

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Học viên nộp bài tập
}
