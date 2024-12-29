package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
