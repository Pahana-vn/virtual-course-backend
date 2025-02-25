package com.mytech.virtualcourse.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class InstructorStatisticsDTO {
    private Long instructorId;
    private String instructorName;
    private int totalCourses;
    private int totalPublishedCourses;
    private int totalPendingCourses;
    private int totalStudents;
    private int totalPurchasedCourses;
    private int totalTransactions;
    private int totalDeposits;
    private int totalWithdrawals;
//    private int totalReviews;
    private BigDecimal balance;
    private String avatarImage;
}
