package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    // Find reviews by course ID
    List<Review> findByCourseId(Long courseId);
    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    // Find reviews by student ID
    Page<Review> findByStudentId(Long studentId, Pageable pageable);

    // Find reviews by instructor ID
    Page<Review> findByInstructorId(Long instructorId, Pageable pageable);

    // Find reviews by course ID and rating
    Page<Review> findByCourseIdAndRating(Long courseId, Integer rating, Pageable pageable);

    // Find reviews by student ID and rating
    Page<Review> findByStudentIdAndRating(Long studentId, Integer rating, Pageable pageable);

    // Find reviews by instructor ID and rating
    Page<Review> findByInstructorIdAndRating(Long instructorId, Integer rating, Pageable pageable);

    // Find reviews by course ID and student ID
    Page<Review> findByCourseIdAndStudentId(Long courseId, Long studentId, Pageable pageable);

    // Find reviews by course ID, student ID and rating
    Page<Review> findByCourseIdAndStudentIdAndRating(Long courseId, Long studentId, Integer rating, Pageable pageable);

    // Find reviews by rating
    Page<Review> findByRating(Integer rating, Pageable pageable);

    // Count reviews by rating
    Long countByRating(Integer rating);

    // Get average rating for an instructor
    @Query("SELECT AVG(r.rating) FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId")
    Double getAverageRatingForInstructor(@Param("instructorId") Long instructorId);

    // Count reviews by instructor ID and rating
    @Query("SELECT COUNT(r) FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId AND r.rating = :rating")
    Long countReviewsByInstructorIdAndRating(@Param("instructorId") Long instructorId, @Param("rating") Integer rating);

    // Count reviews by instructor ID and date range
    @Query("SELECT COUNT(r) FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId AND r.createdAt BETWEEN :startDate AND :endDate")
    Long countReviewsByInstructorIdAndDateRange(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Custom query to find reviews within a date range
    @Query("SELECT r FROM Review r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<Review> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Custom query to find reviews by course ID within a date range
    @Query("SELECT r FROM Review r WHERE r.course.id = :courseId AND r.createdAt BETWEEN :startDate AND :endDate")
    List<Review> findByCourseIdAndDateRange(
            @Param("courseId") Long courseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Custom query to find reviews by instructor ID within a date range
    @Query("SELECT r FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId AND r.createdAt BETWEEN :startDate AND :endDate")
    List<Review> findByInstructorIdAndDateRange(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Custom query to get average rating for a course
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double getAverageRatingForCourse(@Param("courseId") Long courseId);

    // Custom query to count reviews by course ID
    @Query("SELECT COUNT(r) FROM Review r WHERE r.course.id = :courseId")
    Long countReviewsByCourseId(@Param("courseId") Long courseId);

    // Check if a student has already reviewed a course
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    // Find the most recent reviews
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find the most recent reviews for a course
    Page<Review> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);

    // Find the most recent reviews for an instructor
    @Query("SELECT r FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId ORDER BY r.createdAt DESC")
    Page<Review> findByInstructorIdOrderByCreatedAtDesc(@Param("instructorId") Long instructorId, Pageable pageable);

    // Find the highest rated reviews
    Page<Review> findAllByOrderByRatingDesc(Pageable pageable);

    // Find the lowest rated reviews
    Page<Review> findAllByOrderByRatingAsc(Pageable pageable);

    // Count reviews by rating range
    @Query("SELECT COUNT(r) FROM Review r WHERE r.rating BETWEEN :minRating AND :maxRating")
    Long countByRatingBetween(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);

    // Get average rating for all reviews
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getAverageRatingForAllReviews();

    // Get average rating by date range
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    Double getAverageRatingByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}