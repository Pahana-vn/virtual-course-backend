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
    private Long id;
    private String content;
    private Timestamp sentAt;
    private NotificationType type;
    private Boolean isRead;
    private Long userId;
    private Long courseId;
    private Long paymentId;
}
