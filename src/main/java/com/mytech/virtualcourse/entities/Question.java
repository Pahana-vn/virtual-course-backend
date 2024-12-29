package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question")
public class Question extends AbstractEntity {

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private QuestionType type; // SINGLE or MULTIPLE

    @Column(name = "marks", nullable = false, columnDefinition = "INTEGER CHECK (marks >= 0)")
    private Integer marks;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Khóa học chứa câu hỏi

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = true)
    private Test test;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerOption> answerOptions;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> studentAnswers;
}
