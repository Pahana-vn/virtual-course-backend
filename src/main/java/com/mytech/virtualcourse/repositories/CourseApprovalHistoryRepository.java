package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.CourseApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseApprovalHistoryRepository extends JpaRepository<CourseApprovalHistory, Long> {
    List<CourseApprovalHistory> findByCourseIdOrderByCreatedAtDesc(Long courseId);
}
