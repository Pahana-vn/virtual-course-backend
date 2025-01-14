// src/main/java/com/mytech/virtualcourse/services/NotificationService.java

package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.NotificationDTO;
import com.mytech.virtualcourse.entities.Notification;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.NotificationMapper;
import com.mytech.virtualcourse.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * Tạo và gửi thông báo.
     *
     * @param dto Dữ liệu thông báo.
     * @return NotificationDTO đã được lưu.
     */
    public NotificationDTO createNotification(NotificationDTO dto) {
        Notification notification = notificationMapper.toEntity(dto);
        notification.setSentAt(Timestamp.from(Instant.now()));
        notification.setIsRead(false);
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    /**
     * Lấy tất cả thông báo của người dùng.
     *
     * @param userId ID của người dùng.
     * @return Danh sách NotificationDTO.
     */
    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Đánh dấu thông báo là đã đọc.
     *
     * @param notificationId ID của thông báo.
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Xóa thông báo.
     *
     * @param notificationId ID của thông báo.
     */
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Gửi thông báo khi có sự kiện mới.
     *
     * @param userId    ID của người dùng.
     * @param content   Nội dung thông báo.
     * @param type      Loại thông báo.
     * @param courseId  (Tuỳ chọn) ID của khóa học liên quan.
     * @param paymentId (Tuỳ chọn) ID của thanh toán liên quan.
     */
    public void sendNotification(Long userId, String content, NotificationType type, Long courseId, Long paymentId) {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(userId);
        dto.setContent(content);
        dto.setType(type);
        dto.setCourseId(courseId);
        dto.setPaymentId(paymentId);
        createNotification(dto);

        // Tích hợp Push Notifications (ví dụ: sử dụng FCM)
        // sendPushNotification(userId, content);
    }
    // /**
    //  * Fetch all notifications for a given instructor.
    //  *
    //  * @param instructorId ID of the instructor.
    //  * @return List of NotificationDTO.
    //  */
    // public List<NotificationDTO> getNotificationsByInstructorId(Long instructorId) {
    //     List<Notification> notifications = notificationRepository.findByUserInstructorId(instructorId);
    //     if (notifications.isEmpty()) {
    //         throw new ResourceNotFoundException("No notifications found for instructor with id: " + instructorId);
    //     }
    //     return notifications.stream()
    //             .map(notificationMapper::toDTO)
    //             .collect(Collectors.toList());
    // }
}
