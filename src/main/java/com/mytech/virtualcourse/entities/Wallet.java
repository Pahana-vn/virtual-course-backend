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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusWallet statusWallet;

    @Column(name = "min_limit")
    private BigDecimal minLimit;

    @Column(name = "last_updated", nullable = false)
    private Timestamp lastUpdated;

    @OneToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;
}
