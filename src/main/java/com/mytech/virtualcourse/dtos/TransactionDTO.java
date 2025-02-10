// src/main/java/com/mytech/virtualcourse/dtos/TransactionDTO.java

package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.enums.PaymentStatus;
import com.mytech.virtualcourse.enums.TransactionStatus;
import com.mytech.virtualcourse.enums.TransactionType;
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
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private Timestamp processedAt;
    private Long walletId;
    private Long paymentId;
}
