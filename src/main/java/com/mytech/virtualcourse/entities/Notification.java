package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends AbstractEntity {

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung thông báo

    @Column(name = "sent_at", nullable = false)
    private Timestamp sentAt; // Thời gian gửi thông báo

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type; // Loại thông báo (e.g., PAYMENT, ENROLLMENT)

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // Đã đọc hay chưa

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user; // Người nhận thông báo

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course; // Liên kết thông báo với khóa học

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment; // Liên kết thông báo với thanh toán
}
