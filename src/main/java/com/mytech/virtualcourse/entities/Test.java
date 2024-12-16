package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.StatusTest;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test")
public class Test extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks; // Tổng điểm của bài kiểm tra

    @Column(name = "pass_percentage", nullable = false)
    private Integer passPercentage = 60; // Tỷ lệ điểm cần đạt để pass, mặc định là 60%

    @Column(name = "duration", nullable = false)
    private Integer duration; // Thời lượng làm bài (phút)

    @Column(name = "is_final_test", nullable = false)
    private Boolean isFinalTest = false; // Đánh dấu đây là bài kiểm tra cuối khóa

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusTest statusTest;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Bài kiểm tra thuộc khóa học nào

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor; // Giảng viên tạo bài kiểm tra

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions; // Danh sách câu hỏi thuộc bài kiểm tra

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentTestSubmission> submissions; // Danh sách bài nộp của học viên
}
