package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_answer")
public class StudentAnswer extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private StudentTestSubmission submission; // Bài nộp mà câu trả lời này thuộc về

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // Câu hỏi liên quan đến câu trả lời

    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    private AnswerOption selectedOption; // Đáp án mà học viên đã chọn
}
