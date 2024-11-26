package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.entities.AbstractEntity;
import com.mytech.virtualcourse.entities.Student;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_badge")
public class StudentBadge extends AbstractEntity {

    @Column(name = "level", nullable = false)
    private Integer level; // Cấp độ huy hiệu

    @Column(name = "badge_name", nullable = false)
    private String badgeName; // Tên huy hiệu

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Mô tả về huy hiệu

    @Column(name = "icon_url")
    private String iconUrl; // URL của biểu tượng huy hiệu


    @Column(name = "earned_at", nullable = false)
    private Timestamp earnedAt; // Thời điểm đạt được huy hiệu

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Học viên đạt được huy hiệu này
}
