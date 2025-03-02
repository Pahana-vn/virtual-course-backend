// src/main/java/com/mytech/virtualcourse/repositories/ReviewRepository.java

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Review;
import com.mytech.virtualcourse.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCourseId(Long courseId);
    Page<Review> findByCourseId(Long courseId, Pageable pageable);
}
