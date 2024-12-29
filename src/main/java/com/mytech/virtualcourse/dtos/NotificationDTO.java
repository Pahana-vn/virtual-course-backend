package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.enums.NotificationType;
import lombok.*;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDTO {

    private Long id; // ID của thông báo
    private String content; // Nội dung thông báo
    private Timestamp sentAt; // Thời gian gửi thông báo
    private NotificationType type; // Loại thông báo
    private Boolean isRead; // Trạng thái đã đọc
    private Long userId; // ID của người nhận thông báo
    private Long courseId; // ID của khóa học (nếu có)
    private Long paymentId; // ID của thanh toán (nếu có)
}
