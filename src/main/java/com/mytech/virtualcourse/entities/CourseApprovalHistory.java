package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.ECourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "course_approval_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class CourseApprovalHistory extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Account reviewer;

    @Enumerated(EnumType.STRING)
    private ECourseStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;
}
