package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.PromotionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_promotion")
public class CoursePromotion extends AbstractEntity {

    @Column(name = "promotion_price", nullable = false)
    private BigDecimal promotionPrice;

    @Column(name = "from_date", nullable = false)
    private Timestamp fromDate;

    @Column(name = "to_date", nullable = false)
    private Timestamp toDate;

    @Column(name = "status", nullable = false)
    private PromotionStatus promotionStatus; // ACTIVE, EXPIRED, UPCOMING

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
