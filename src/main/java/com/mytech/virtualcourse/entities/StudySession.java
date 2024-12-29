package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.entities.Student;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_session")
public class StudySession extends AbstractEntity {

    @Column(name = "session_date", nullable = false)
    private Timestamp sessionDate; // Ngày học

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime; // Giờ bắt đầu

    @Column(name = "end_time", nullable = false)
    private Timestamp endTime; // Giờ kết thúc

    @Column(name = "duration_study", nullable = false)
    private Integer durationStudy; // Tổng thời gian học (phút)

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Học viên tham gia buổi học

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Khóa học mà buổi học này thuộc về

    @ManyToOne
    @JoinColumn(name = "learning_progress_id", nullable = false)
    private LearningProgress learningProgress;

    @ManyToOne
    @JoinColumn(name = "learning_schedule_id", nullable = false)
    private LearningSchedule learningSchedule;
}
