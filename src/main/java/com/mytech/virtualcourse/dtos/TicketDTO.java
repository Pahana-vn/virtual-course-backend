// src/main/java/com/mytech/virtualcourse/dtos/TicketDTO.java

package com.mytech.virtualcourse.dtos;

import lombok.*;

/**
 * DTO cho SupportTicket.
 * Lưu ý: ta có `creatorAccountId` để từ phía client gửi lên,
 * còn `creatorName` (hoặc email) để trả về hiển thị.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TicketDTO {
    private Long id;
    private String title;
    private String description;
    private String status; // OPEN / IN_PROGRESS / RESOLVED / CLOSED / ...

    private Long creatorAccountId; // ID của account tạo ticket
    private String creatorEmail;
    private Long assignedToAccountId; // Nếu muốn gán cho người xử lý
    private String assignedToEmail;   // Hiển thị email nếu có
    // Có thể thêm field assignedToAccountId, assignedToEmail,...
}
