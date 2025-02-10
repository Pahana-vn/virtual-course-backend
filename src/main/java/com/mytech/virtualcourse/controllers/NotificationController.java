// src/main/java/com/mytech/virtualcourse/controllers/NotificationController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.NotificationDTO;
import com.mytech.virtualcourse.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Tạo thông báo mới
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.createNotification(dto));
    }

    // Lấy tất cả thông báo của một người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    // /**
    //  * GET /api/notifications/instructor/{instructorId}
    //  * Fetch all notifications for a specific instructor.
    //  *
    //  * @param instructorId ID of the instructor.
    //  * @return List of NotificationDTO.
    //  */
    // @GetMapping("/instructor/{instructorId}")
    // public ResponseEntity<List<NotificationDTO>> getNotificationsByInstructor(@PathVariable Long instructorId) {
    //     List<NotificationDTO> notifications = notificationService.getNotificationsByInstructorId(instructorId);
    //     return ResponseEntity.ok(notifications);
    // }

    // Đánh dấu thông báo là đã đọc
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // Xóa thông báo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
