package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.NotificationDTO;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.createNotification(dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/all/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<NotificationDTO>> getAllNotificationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getAllNotificationsPaginated(page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<Page<NotificationDTO>> getNotificationsByUserPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserPaginated(userId, page, size));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(notificationService.getNotificationsByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/paginated")
    public ResponseEntity<Page<NotificationDTO>> getNotificationsByCoursePaginated(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotificationsByCoursePaginated(courseId, page, size));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<Integer> markAllAsReadForUser(@PathVariable Long userId) {
        int count = notificationService.markAllAsReadForUser(userId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/user/{userId}/type/{type}/mark-all-read")
    public ResponseEntity<Integer> markAllAsReadForUserByType(
            @PathVariable Long userId,
            @PathVariable NotificationType type) {
        int count = notificationService.markAllAsReadForUserByType(userId, type);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}/delete-all-read")
    public ResponseEntity<Integer> deleteAllReadForUser(@PathVariable Long userId) {
        int count = notificationService.deleteAllReadForUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/unread/paginated")
    public ResponseEntity<Page<NotificationDTO>> getUnreadNotificationsByUserPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserPaginated(userId, page, size));
    }

    @GetMapping("/user/{userId}/count-unread")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userId));
    }

    @GetMapping("/user/{userId}/type/{type}/count")
    public ResponseEntity<Long> countNotificationsByType(
            @PathVariable Long userId,
            @PathVariable NotificationType type) {
        return ResponseEntity.ok(notificationService.countNotificationsByType(userId, type));
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<NotificationDTO>> getRecentNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getRecentNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/recent/paginated")
    public ResponseEntity<List<NotificationDTO>> getRecentNotificationsByUserPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getRecentNotificationsByUserPaginated(userId, page, size));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByType(@PathVariable NotificationType type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }

    @GetMapping("/type/{type}/paginated")
    public ResponseEntity<Page<NotificationDTO>> getNotificationsByTypePaginated(
            @PathVariable NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotificationsByTypePaginated(type, page, size));
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserAndType(
            @PathVariable Long userId,
            @PathVariable NotificationType type) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserAndType(userId, type));
    }

    @GetMapping("/user/{userId}/type/{type}/paginated")
    public ResponseEntity<Page<NotificationDTO>> getNotificationsByUserAndTypePaginated(
            @PathVariable Long userId,
            @PathVariable NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserAndTypePaginated(userId, type, page, size));
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(notificationService.getNotificationsByPayment(paymentId));
    }

    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<NotificationDTO>> searchNotificationsByContent(
            @PathVariable Long userId,
            @RequestParam String searchTerm) {
        return ResponseEntity.ok(notificationService.searchNotificationsByContent(userId, searchTerm));
    }

    @GetMapping("/user/{userId}/search/paginated")
    public ResponseEntity<Page<NotificationDTO>> searchNotificationsByContentPaginated(
            @PathVariable Long userId,
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.searchNotificationsByContentPaginated(userId, searchTerm, page, size));
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByDateRange(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(notificationService.getNotificationsByDateRange(userId, startDate, endDate));
    }

    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getNotificationStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationStatistics(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PutMapping("/{id}/content")
    public ResponseEntity<NotificationDTO> updateNotificationContent(
            @PathVariable Long id,
            @RequestParam String content) {
        return ResponseEntity.ok(notificationService.updateNotificationContent(id, content));
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationDTO> sendNotification(
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam NotificationType type,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long paymentId) {

        System.out.println("Received notification request:");
        System.out.println("userId: " + userId);
        System.out.println("content: " + content);
        System.out.println("type: " + type);
        System.out.println("courseId: " + courseId);
        System.out.println("paymentId: " + paymentId);

        NotificationDTO notification = notificationService.sendNotification(userId, content, type, courseId, paymentId);
        System.out.println("Notification created with ID: " + notification.getId());

        return ResponseEntity.ok(notification);
    }

    @PostMapping("/send-multiple")
    public ResponseEntity<Integer> sendNotificationToMultipleUsers(
            @RequestParam List<Long> userIds,
            @RequestParam String content,
            @RequestParam NotificationType type,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long paymentId) {
        int count = notificationService.sendNotificationToMultipleUsers(userIds, content, type, courseId, paymentId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/send-all")
    public ResponseEntity<Integer> sendNotificationToAllUsers(
            @RequestParam String content,
            @RequestParam NotificationType type) {
        int count = notificationService.sendNotificationToAllUsers(content, type);
        return ResponseEntity.ok(count);
    }

//    @PostMapping("/send-course-enrollees")
//    public ResponseEntity<Integer> sendNotificationToCourseEnrollees(
//            @RequestParam Long courseId,
//            @RequestParam String content,
//            @RequestParam NotificationType type) {
//        int count = notificationService.sendNotificationToCourseEnrollees(courseId, content, type);
//        return ResponseEntity.ok(count);
//    }

    @PostMapping("/schedule")
    public ResponseEntity<Void> scheduleNotification(
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam NotificationType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long paymentId) {
        notificationService.scheduleNotification(userId, content, type, scheduledTime, courseId, paymentId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

//    @GetMapping("/admin/type/{type}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<NotificationDTO>> getNotificationsByTypeForAdmin(@PathVariable NotificationType type) {
//        return ResponseEntity.ok(notificationService.getNotificationsByTypeForAdmin(type));
//    }

}