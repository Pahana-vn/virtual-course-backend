package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Wallet;

import java.math.BigDecimal;
import java.util.Optional;

import com.mytech.virtualcourse.enums.StatusWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByInstructorId(Long instructorId);

    // Add these methods to your existing WalletRepository interface

    long countByStatusWallet(StatusWallet statusWallet);

    @Query("SELECT SUM(w.balance) FROM Wallet w")
    BigDecimal sumAllBalances();
}