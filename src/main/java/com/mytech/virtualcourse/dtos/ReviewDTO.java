// src/main/java/com/mytech/virtualcourse/dtos/ReviewDTO.java

package com.mytech.virtualcourse.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO để trao đổi review (đánh giá) giữa client - server.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDTO {
    private Long id;

    private Long studentId; // Thêm trường này
    private Long instructorId; // Thêm trường này nếu cần

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    private String reply; // Giảng viên trả lời

}
// Getters and Setters
