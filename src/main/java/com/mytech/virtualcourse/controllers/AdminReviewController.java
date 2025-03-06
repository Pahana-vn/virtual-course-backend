package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.ReviewDTO;
import com.mytech.virtualcourse.dtos.ReviewStatisticsDTO;
import com.mytech.virtualcourse.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reviews")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Get all reviews with pagination and filtering
     *
     * @param courseId Optional filter by course ID
     * @param studentId Optional filter by student ID
     * @param rating Optional filter by rating
     * @param pageable Pagination information
     * @return Page of ReviewDTO
     */
    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer rating,
            Pageable pageable) {

        Page<ReviewDTO> reviews = reviewService.getAllReviews(courseId, studentId, rating, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get review statistics
     *
     * @param courseId Optional filter by course ID
     * @param instructorId Optional filter by instructor ID
     * @param startDate Optional filter by start date
     * @param endDate Optional filter by end date
     * @return ReviewStatisticsDTO
     */
    @GetMapping("/statistics")
    public ResponseEntity<ReviewStatisticsDTO> getReviewStatistics(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ReviewStatisticsDTO statistics = reviewService.getReviewStatistics(courseId, instructorId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get a specific review by ID
     *
     * @param reviewId ID of the review
     * @return ReviewDTO
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long reviewId) {
        ReviewDTO review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    /**
     * Moderate a review (admin can update any review)
     *
     * @param reviewId ID of the review
     * @param dto Updated review data
     * @return Updated ReviewDTO
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> moderateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDTO dto) {

        ReviewDTO updated = reviewService.moderateReview(reviewId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a review
     *
     * @param reviewId ID of the review
     * @return No content
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}