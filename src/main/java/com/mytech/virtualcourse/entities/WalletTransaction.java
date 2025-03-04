package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // CREDIT, DEBIT

    @Column(name = "description")
    private String description;

    @Column(name = "reference_id")
    private String referenceId; // ID tham chiếu (ví dụ: ID khóa học, ID rút tiền)

    @Column(name = "status", nullable = false)
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}