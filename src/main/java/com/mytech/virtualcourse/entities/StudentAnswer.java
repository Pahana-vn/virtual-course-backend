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
    private StudentTestSubmission submission;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    private AnswerOption selectedOption;
}
