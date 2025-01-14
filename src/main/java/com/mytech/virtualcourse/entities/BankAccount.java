package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.mytech.virtualcourse.enums.BankName;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_account")
public class BankAccount extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_name", nullable = false)
    private BankName bankName;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @OneToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

//    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Transaction> transactions;
}
