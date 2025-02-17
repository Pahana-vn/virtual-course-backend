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

    @Column(name = "marks_obtained", nullable = false)
    private Integer marksObtained;

    @Column(name = "passed", nullable = false)
    private Boolean passed;

    @Column(name = "duration", nullable = false)
    private Integer durationTest;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Học viên đã nộp bài kiểm tra

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> answers; // Câu trả lời của học viên

    @OneToOne(mappedBy = "finalTestSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private LearningProgress learningProgress;

    public String getTestTitle() {
        return test.getTitle();
    }

    // ✅ Getter để lấy số câu hỏi từ Test
    public int getTotalQuestions() {
        return test.getQuestions().size();
    }
}

