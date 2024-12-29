package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.StatusWallet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet extends AbstractEntity {

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "status", nullable = false)
    private StatusWallet statusWallet; // e.g., ACTIVE, SUSPENDED, CLOSED

    @Column(name = "max_limit")
    private BigDecimal maxLimit; // số dư max trong ví

    @Column(name = "last_updated", nullable = false)
    private Timestamp lastUpdated; // Thời gian cuối cùng cập nhật ví

    @OneToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;
}
