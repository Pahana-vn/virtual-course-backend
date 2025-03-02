package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Notification;
import com.mytech.virtualcourse.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find notifications by user ID
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.sentAt DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);

    // Find notifications by user ID with pagination
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.sentAt DESC")
    Page<Notification> findByUserIdPaginated(@Param("userId") Long userId, Pageable pageable);

    // Find unread notifications by user ID
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.sentAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    // Find unread notifications by user ID with pagination
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.sentAt DESC")
    Page<Notification> findUnreadByUserIdPaginated(@Param("userId") Long userId, Pageable pageable);

    // Find notifications by course ID
    @Query("SELECT n FROM Notification n WHERE n.course.id = :courseId ORDER BY n.sentAt DESC")
    List<Notification> findByCourseId(@Param("courseId") Long courseId);

    // Find notifications by course ID with pagination
    @Query("SELECT n FROM Notification n WHERE n.course.id = :courseId ORDER BY n.sentAt DESC")
    Page<Notification> findByCourseIdPaginated(@Param("courseId") Long courseId, Pageable pageable);

    // Find notifications by type
    List<Notification> findByTypeOrderBySentAtDesc(NotificationType type);

    // Find notifications by type with pagination
    Page<Notification> findByTypeOrderBySentAtDesc(NotificationType type, Pageable pageable);

    // Find notifications by type for a specific user
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.sentAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);

    // Find notifications by type for a specific user with pagination
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.sentAt DESC")
    Page<Notification> findByUserIdAndTypePaginated(@Param("userId") Long userId, @Param("type") NotificationType type, Pageable pageable);

    // Count unread notifications for a user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);

    // Count notifications by type for a user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.type = :type")
    Long countByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);

    // Find recent notifications for a user (last 30 days)
    @Query(value = "SELECT * FROM notification n WHERE n.user_id = :userId AND n.sent_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) ORDER BY n.sent_at DESC", nativeQuery = true)
    List<Notification> findRecentByUserId(@Param("userId") Long userId);

    // Find recent notifications for a user (last 30 days) with pagination
    @Query(value = "SELECT * FROM notification n WHERE n.user_id = :userId AND n.sent_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) ORDER BY n.sent_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Notification> findRecentByUserIdPaginated(@Param("userId") Long userId, @Param("limit") int limit, @Param("offset") int offset);

    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadForUser(@Param("userId") Long userId);

    // Mark all notifications of a specific type as read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.type = :type AND n.isRead = false")
    int markAllAsReadForUserByType(@Param("userId") Long userId, @Param("type") NotificationType type);

    // Delete all read notifications for a user
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId AND n.isRead = true")
    int deleteAllReadForUser(@Param("userId") Long userId);

    // Find notifications by payment ID
    @Query("SELECT n FROM Notification n WHERE n.payment.id = :paymentId ORDER BY n.sentAt DESC")
    List<Notification> findByPaymentId(@Param("paymentId") Long paymentId);

    // Search notifications by content for a user
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY n.sentAt DESC")
    List<Notification> searchByContentForUser(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    // Search notifications by content for a user with pagination
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY n.sentAt DESC")
    Page<Notification> searchByContentForUserPaginated(@Param("userId") Long userId, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Find notifications within a date range for a user
    @Query(value = "SELECT * FROM notification n WHERE n.user_id = :userId AND n.sent_at BETWEEN :startDate AND :endDate ORDER BY n.sent_at DESC", nativeQuery = true)
    List<Notification> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    Collection<Object> findByType(NotificationType type);
}