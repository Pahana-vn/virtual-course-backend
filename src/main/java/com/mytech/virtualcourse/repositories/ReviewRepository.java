// src/main/java/com/mytech/virtualcourse/repositories/ReviewRepository.java

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy tất cả review của 1 course
    List<Review> findByCourseId(Long courseId);
    Page<Review> findByCourseId(Long courseId, Pageable pageable);
    // List<Review> findByCourseInstructorId(Long instructorId);

    // Hoặc thêm method findByStudentId..., tuỳ nhu cầu
}
