package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Date;
import java.util.Optional;

public interface AdminAccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    Optional<Account> findByResetPasswordToken(String token);
    Optional<Account> findByEmail(String email);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end); // Changed to LocalDateTime
    Object findByUsername(String username);
    @Query("SELECT MIN(a.createdAt) FROM Account a")
    LocalDate getFirstCreatedDate();
}
