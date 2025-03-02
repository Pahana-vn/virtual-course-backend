package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.NotificationDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Notification;
import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.NotificationMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.NotificationRepository;
import com.mytech.virtualcourse.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Create a notification
     * @param dto Notification data
     * @return Saved notification
     */
    public NotificationDTO createNotification(NotificationDTO dto) {
        Notification notification = notificationMapper.toEntity(dto);
        notification.setSentAt(Timestamp.from(Instant.now()));
        notification.setIsRead(false);
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    /**
     * Get all notifications for a user
     * @param userId User ID
     * @return List of notifications
     */
    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated notifications for a user
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated notifications
     */
    public Page<NotificationDTO> getNotificationsByUserPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<Notification> notificationsPage = notificationRepository.findByUserIdPaginated(userId, pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

    /**
     * Mark a notification as read
     * @param notificationId Notification ID
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for a user
     * @param userId User ID
     * @return Number of notifications marked as read
     */
    public int markAllAsReadForUser(Long userId) {
        return notificationRepository.markAllAsReadForUser(userId);
    }

    /**
     * Mark all notifications of a specific type as read for a user
     * @param userId User ID
     * @param type Notification type
     * @return Number of notifications marked as read
     */
    public int markAllAsReadForUserByType(Long userId, NotificationType type) {
        return notificationRepository.markAllAsReadForUserByType(userId, type);
    }

    /**
     * Delete a notification
     * @param notificationId Notification ID
     */
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Delete all read notifications for a user
     * @param userId User ID
     * @return Number of notifications deleted
     */
    public int deleteAllReadForUser(Long userId) {
        return notificationRepository.deleteAllReadForUser(userId);
    }

    /**
     * Send a notification to a user
     * @param userId User ID
     * @param content Notification content
     * @param type Notification type
     * @param courseId Course ID (optional)
     * @param paymentId Payment ID (optional)
     * @return Created notification
     */
    public NotificationDTO sendNotification(Long userId, String content, NotificationType type, Long courseId, Long paymentId) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setContent(content);
        notification.setSentAt(Timestamp.from(Instant.now()));
        notification.setType(type != null ? type : NotificationType.SYSTEM);
        notification.setIsRead(false);
        notification.setUser(user);

        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
            notification.setCourse(course);
        }

        if (paymentId != null) {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
            notification.setPayment(payment);
        }

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    /**
     * Send a notification to multiple users
     * @param userIds List of user IDs
     * @param content Notification content
     * @param type Notification type
     * @param courseId Course ID (optional)
     * @param paymentId Payment ID (optional)
     * @return Number of notifications sent
     */
    public int sendNotificationToMultipleUsers(List<Long> userIds, String content, NotificationType type, Long courseId, Long paymentId) {
        int count = 0;
        for (Long userId : userIds) {
            try {
                sendNotification(userId, content, type, courseId, paymentId);
                count++;
            } catch (Exception e) {
                // Log error but continue with other users
                System.err.println("Failed to send notification to user " + userId + ": " + e.getMessage());
            }
        }
        return count;
    }

    /**
     * Send a notification to all users
     * @param content Notification content
     * @param type Notification type
     * @return Number of notifications sent
     */
    public int sendNotificationToAllUsers(String content, NotificationType type) {
        List<Account> allUsers = accountRepository.findAll();
        int count = 0;
        for (Account user : allUsers) {
            try {
                Notification notification = new Notification();
                notification.setContent(content);
                notification.setSentAt(Timestamp.from(Instant.now()));
                notification.setType(type);
                notification.setIsRead(false);
                notification.setUser(user);
                notificationRepository.save(notification);
                count++;
            } catch (Exception e) {
                // Log error but continue with other users
                System.err.println("Failed to send notification to user " + user.getId() + ": " + e.getMessage());
            }
        }
        return count;
    }

    /**
     * Send a notification to all users enrolled in a course
     * @param courseId Course ID
     * @param content Notification content
     * @param type Notification type
     * @return Number of notifications sent
     */
//    public int sendNotificationToCourseEnrollees(Long courseId, String content, NotificationType type) {
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
//
//        List<Account> enrolledUsers = course.getEnrollments().stream()
//                .map(enrollment -> enrollment.getUser())
//                .collect(Collectors.toList());
//
//        int count = 0;
//        for (Account user : enrolledUsers) {
//            try {
//                Notification notification = new Notification();
//                notification.setContent(content);
//                notification.setSentAt(Timestamp.from(Instant.now()));
//                notification.setType(type);
//                notification.setIsRead(false);
//                notification.setUser(user);
//                notification.setCourse(course);
//                notificationRepository.save(notification);
//                count++;
//            } catch (Exception e) {
//                // Log error but continue with other users
//                System.err.println("Failed to send notification to user " + user.getId() + ": " + e.getMessage());
//            }
//        }
//        return count;
//    }

    /**
     * Get notifications by course ID
     * @param courseId Course ID
     * @return List of notifications
     */
    public List<NotificationDTO> getNotificationsByCourse(Long courseId) {
        List<Notification> notifications = notificationRepository.findByCourseId(courseId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated notifications by course ID
     * @param courseId Course ID
     * @param page Page number
     * @param size Page size
     * @return Paginated notifications
     */
    public Page<NotificationDTO> getNotificationsByCoursePaginated(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<Notification> notificationsPage = notificationRepository.findByCourseIdPaginated(courseId, pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

    /**
     * Get unread notifications for a user
     * @param userId User ID
     * @return List of unread notifications
     */
    public List<NotificationDTO> getUnreadNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated unread notifications for a user
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated unread notifications
     */
    public Page<NotificationDTO> getUnreadNotificationsByUserPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<Notification> notificationsPage = notificationRepository.findUnreadByUserIdPaginated(userId, pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

    /**
     * Count unread notifications for a user
     * @param userId User ID
     * @return Count of unread notifications
     */
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Count notifications by type for a user
     * @param userId User ID
     * @param type Notification type
     * @return Count of notifications
     */
    public Long countNotificationsByType(Long userId, NotificationType type) {
        return notificationRepository.countByUserIdAndType(userId, type);
    }

    /**
     * Get recent notifications for a user (last 30 days)
     * @param userId User ID
     * @return List of recent notifications
     */
    public List<NotificationDTO> getRecentNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository.findRecentByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recent notifications for a user (last 30 days) with pagination
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated recent notifications
     */
    public List<NotificationDTO> getRecentNotificationsByUserPaginated(Long userId, int page, int size) {
        int offset = page * size;
        List<Notification> notifications = notificationRepository.findRecentByUserIdPaginated(userId, size, offset);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get notifications by type
     * @param type Notification type
     * @return List of notifications
     */
    public List<NotificationDTO> getNotificationsByType(NotificationType type) {
        List<Notification> notifications = notificationRepository.findByTypeOrderBySentAtDesc(type);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated notifications by type
     * @param type Notification type
     * @param page Page number
     * @param size Page size
     * @return Paginated notifications
     */
    public Page<NotificationDTO> getNotificationsByTypePaginated(NotificationType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<Notification> notificationsPage = notificationRepository.findByTypeOrderBySentAtDesc(type, pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

    /**
     * Get notifications by type for a user
     * @param userId User ID
     * @param type Notification type
     * @return List of notifications
     */
    public List<NotificationDTO> getNotificationsByUserAndType(Long userId, NotificationType type) {
        List<Notification> notifications = notificationRepository.findByUserIdAndType(userId, type);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated notifications by type for a user
     * @param userId User ID
     * @param type Notification type
     * @param page Page number
     * @param size Page size
     * @return Paginated notifications
     */
    public Page<NotificationDTO> getNotificationsByUserAndTypePaginated(Long userId, NotificationType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<Notification> notificationsPage = notificationRepository.findByUserIdAndTypePaginated(userId, type, pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

    /**
     * Get notifications by payment ID
     * @param paymentId Payment ID
     * @return List of notifications
     */
    public List<NotificationDTO> getNotificationsByPayment(Long paymentId) {
        List<Notification> notifications = notificationRepository.findByPaymentId(paymentId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search notifications by content for a user
     * @param userId User ID
     * @param searchTerm Search term
     * @return List of matching notifications
     */
    public List<NotificationDTO> searchNotificationsByContent(Long userId, String searchTerm) {
        List<Notification> notifications = notificationRepository.searchByContentForUser(userId, searchTerm);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search notifications by content for a user with pagination
     * @param userId User ID
     * @param searchTerm Search term
     * @param page Page number
     * @param size Page size
     * @return Paginated matching notifications
     */
    public Page<NotificationDTO> searchNotificationsByContentPaginated(Long userId, String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<Notification> notificationsPage = notificationRepository.searchByContentForUserPaginated(userId, searchTerm, pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

    /**
     * Get notifications within a date range for a user
     * @param userId User ID
     * @param startDate Start date (yyyy-MM-dd)
     * @param endDate End date (yyyy-MM-dd)
     * @return List of notifications within the date range
     */
    public List<NotificationDTO> getNotificationsByDateRange(Long userId, String startDate, String endDate) {
        List<Notification> notifications = notificationRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get notification statistics for a user
     * @param userId User ID
     * @return Map containing notification statistics
     */
    public Map<String, Object> getNotificationStatistics(Long userId) {
        Long totalCount = (long) notificationRepository.findByUserId(userId).size();
        Long unreadCount = notificationRepository.countUnreadByUserId(userId);
        Long courseNotificationsCount = (long) notificationRepository.findByUserIdAndType(userId, NotificationType.COURSE).size();
        Long paymentNotificationsCount = (long) notificationRepository.findByUserIdAndType(userId, NotificationType.Payment).size();
        Long systemNotificationsCount = (long) notificationRepository.findByUserIdAndType(userId, NotificationType.SYSTEM).size();

        return Map.of(
                "totalCount", totalCount,
                "unreadCount", unreadCount,
                "courseNotificationsCount", courseNotificationsCount,
                "paymentNotificationsCount", paymentNotificationsCount,
                "systemNotificationsCount", systemNotificationsCount
        );
    }

    /**
     * Get notification by ID
     * @param id Notification ID
     * @return Notification DTO
     */
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return notificationMapper.toDTO(notification);
    }

    /**
     * Update notification content
     * @param id Notification ID
     * @param content New content
     * @return Updated notification
     */
    public NotificationDTO updateNotificationContent(Long id, String content) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        notification.setContent(content);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(updatedNotification);
    }

    /**
     * Send a scheduled notification (to be used with a scheduler)
     * @param userId User ID
     * @param content Notification content
     * @param type Notification type
     * @param scheduledTime Time to send the notification
     * @param courseId Course ID (optional)
     * @param paymentId Payment ID (optional)
     */
    public void scheduleNotification(Long userId, String content, NotificationType type,
                                     LocalDateTime scheduledTime, Long courseId, Long paymentId) {
        // This method would be called by a scheduler at the specified time
        // For now, we'll just create the notification with the current time
        sendNotification(userId, content, type, courseId, paymentId);

        // In a real implementation, you would use a scheduling framework like Quartz
        // or Spring's @Scheduled annotation to send the notification at the specified time
    }
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<NotificationDTO> getAllNotificationsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notificationsPage = notificationRepository.findAll(pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

//    public List<NotificationDTO> getNotificationsByTypeForAdmin(NotificationType type) {
//        return notificationRepository.findByType(type).stream()
//                .map(notificationMapper::toDTO)
//                .collect(Collectors.toList());
//    }
}