// src/main/java/com/mytech/virtualcourse/controllers/ReviewController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.ReviewDTO;
import com.mytech.virtualcourse.repositories.ReviewRepository;
import com.mytech.virtualcourse.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;
    // Tạo 1 review
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO dto) {
        ReviewDTO created = reviewService.createReview(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy tất cả đánh giá của một khóa học với phân trang
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByCourse(@PathVariable Long courseId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseId, pageable));
    }

    // /**
    //  * GET /api/reviews/instructor/{instructorId}
    //  * Fetch all reviews for a specific instructor.
    //  *
    //  * @param instructorId ID of the instructor.
    //  * @return List of ReviewDTO.
    //  */
    // @GetMapping("/instructor/{instructorId}")
    // public ResponseEntity<List<ReviewDTO>> getReviewsByInstructor(@PathVariable Long instructorId) {
    //     List<ReviewDTO> reviews = reviewService.getReviewsByInstructorId(instructorId);
    //     return ResponseEntity.ok(reviews);
    // }

    // Xoá review
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or @reviewService.isReviewOwner(#reviewId, principal.id)")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // Sửa review
    @PutMapping("/{reviewId}")
    @PreAuthorize("@reviewService.isReviewOwner(#reviewId, principal.id)")
    public ResponseEntity<ReviewDTO> updateReview(@Valid @PathVariable Long reviewId, @RequestBody ReviewDTO dto) {
        ReviewDTO updated = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(updated);
    }

}
