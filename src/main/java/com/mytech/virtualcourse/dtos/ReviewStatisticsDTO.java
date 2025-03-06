package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewStatisticsDTO {
    // Overall statistics
    private Long totalReviews;
    private Double averageRating;
    private Long totalPositiveReviews; // 4-5 stars
    private Long totalNegativeReviews; // 1-2 stars

    // Rating distribution
    private Map<Integer, Long> ratingDistribution; // Key: rating (1-5), Value: count

    // Time-based statistics
    private Map<String, MonthlyReviewData> monthlyReviews; // Key: month (e.g., "2023-01"), Value: data

    // Top rated courses
    private List<TopRatedItemDTO> topCourses;

    // Top rated instructors
    private List<TopRatedItemDTO> topInstructors;

    // Nested class for monthly data
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyReviewData {
        private Long count;
        private Double averageRating;
    }

    // Nested class for top rated items
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopRatedItemDTO {
        private Long id;
        private String title; // Course title or instructor name
        private Double averageRating;
        private Long reviewCount;
    }
}