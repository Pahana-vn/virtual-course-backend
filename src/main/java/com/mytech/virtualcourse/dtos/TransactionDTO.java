package com.mytech.virtualcourse.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private String transactionType;
    private String description;
    private String referenceId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}