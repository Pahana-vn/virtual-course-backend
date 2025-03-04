// src/main/java/com/mytech/virtualcourse/repositories/ReviewRepository.java

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Review;
import com.mytech.virtualcourse.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy tất cả review của 1 course
    List<Review> findByCourseId(Long courseId);
    Page<Review> findByCourseId(Long courseId, Pageable pageable);
    // List<Review> findByCourseInstructorId(Long instructorId);
    @Query("SELECT AVG(r.rating) FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId")
    Double getAverageRatingForInstructor(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(r) FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId AND r.rating = :rating")
    Long countReviewsByInstructorIdAndRating(@Param("instructorId") Long instructorId, @Param("rating") Integer rating);

    @Query("SELECT COUNT(r) FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId AND r.createdAt BETWEEN :startDate AND :endDate")
    Long countReviewsByInstructorIdAndDateRange(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Page<Review> findByInstructorId(Long instructorId, Pageable pageable);

    Page<Review> findByInstructorIdAndRating(Long instructorId, Integer rating, Pageable pageable);

}
