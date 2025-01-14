package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.enums.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private Timestamp paymentDate;
    private PaymentMethod paymentMethod;
    private TransactionStatus transactionStatus;
    private Long studentId;
    private Long courseId;
}
