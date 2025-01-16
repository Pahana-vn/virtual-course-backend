package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessageDTO {
    private Long senderAccountId;
    private Long receiverAccountId;
    private String content;
    private String type;
    private String senderName;
    private String receiverName;
    private String senderAvatar;
    private String receiverAvatar;
    private OffsetDateTime timestamp;
}
