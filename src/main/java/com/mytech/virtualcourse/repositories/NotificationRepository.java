// src/main/java/com/mytech/virtualcourse/repositories/NotificationRepository.java

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Notification;
import com.mytech.virtualcourse.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
    List<Notification> findByType(NotificationType type);
    // List<Notification> findByUserInstructorId(Long instructorId);

}
