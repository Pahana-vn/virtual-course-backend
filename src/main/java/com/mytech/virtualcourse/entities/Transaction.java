// src/main/java/com/mytech/virtualcourse/entities/Transaction.java

package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.TransactionStatus;
import com.mytech.virtualcourse.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction extends AbstractEntity {

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // e.g., DEPOSIT, WITHDRAWAL

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus transactionStatus;

    @Column(name = "processed_at")
    private Timestamp processedAt; // Optional: Thời gian xử lý thành công

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment; // Đổi từ Long sang Payment
}
