package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    Optional<Account> findByResetPasswordToken(String token);
    Optional<Account> findByEmail(String email);}

