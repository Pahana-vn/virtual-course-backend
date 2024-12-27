//package com.mytech.virtualcourse.services;
//
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//@Transactional
//public class ReviewService {
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    public List<ReviewDTO> getAllReviews() {
//        List<Review> reviews = reviewRepository.findAll();
//        return reviews.stream().map(r -> mapToDTO(r)).collect(Collectors.toList());
//    }
//
//    public void deleteReview(Long reviewId) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
//        reviewRepository.delete(review);
//    }
//
//    public void hideReview(Long reviewId) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(...);
//        review.setHidden(true);
//        reviewRepository.save(review);
//    }
//
//    public void showReview(Long reviewId) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(...);
//        review.setHidden(false);
//        reviewRepository.save(review);
//    }
//    // ...
//}
//
