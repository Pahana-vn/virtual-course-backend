package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "learning_schedule")
public class LearningSchedule extends AbstractEntity {

    @Column(name = "daily_study_hours", nullable = false)
    private Integer dailyStudyHours; // Số giờ học mỗi ngày

    @Column(name = "preferred_time_start", nullable = false)
    private LocalTime preferredTimeStart; // Thời gian bắt đầu học

    @Column(name = "preferred_time_end", nullable = false)
    private LocalTime preferredTimeEnd; // Thời gian kết thúc học

    @Column(name = "target_completion_date", nullable = false)
    private LocalDate targetCompletionDate; // Ngày mục tiêu hoàn thành khóa học

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Học viên có lịch học này

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Khóa học liên quan

    @OneToMany(mappedBy = "learningSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudySession> studySessions; //liên kết các buổi học chi tiết với lịch học

}
