package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private String title;
    private TransactionType transactionType;
    private StatusTransaction statusTransaction;
    private PaymentMethod paymentMethod;
    private String paypalPayoutId;
    private Timestamp processedAt;
    private Long walletId;
    private BigDecimal walletBalance;
}
