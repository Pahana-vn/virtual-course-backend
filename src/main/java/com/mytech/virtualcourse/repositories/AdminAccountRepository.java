package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

import java.util.Date;
import java.util.Optional;

public interface AdminAccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    Optional<Account> findByResetPasswordToken(String token);
    Optional<Account> findByEmail(String email);
    long countByCreatedAtBetween(Date createdAt, Date createdAt2); // Chart
    Object findByUsername(String username);

}
