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
@Table(name = "student_test_submission")
public class StudentTestSubmission extends AbstractEntity {

    @Column(name = "submitted_at", nullable = false)
    private Timestamp submittedAt;

    @Column(name = "marks_obtained")
    private Integer marksObtained; // Điểm đạt được

    @Column(name = "passed", nullable = false)
    private Boolean passed; // Học viên có vượt qua bài kiểm tra không

    @Column(name = "duration", nullable = false)
    private Integer durationTest; // Thời gian làm bài (phút)

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test; // Bài kiểm tra mà học viên đã nộp

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Học viên đã nộp bài kiểm tra

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> answers; // Câu trả lời của học viên

    @OneToOne(mappedBy = "finalTestSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private LearningProgress learningProgress;
}
