// src/main/java/com/mytech/virtualcourse/dtos/CourseApprovalDTO.java
package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApprovalDTO {
    private Map<String, Boolean> criteria;
    private String notes;
    private LocalDateTime approvedAt;
    private Long approvedBy;
    private String rejectionReason;
}
