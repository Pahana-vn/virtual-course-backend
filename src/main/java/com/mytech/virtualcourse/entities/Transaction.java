package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.enums.StatusTransaction;
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

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_transaction", nullable = false)
    private StatusTransaction statusTransaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "paypal_payout_id")
    private String paypalPayoutId;

    @Column(name = "processed_at")
    private Timestamp processedAt;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @Column(name = "walletBalance", nullable = false)
    private BigDecimal walletBalance;
}
