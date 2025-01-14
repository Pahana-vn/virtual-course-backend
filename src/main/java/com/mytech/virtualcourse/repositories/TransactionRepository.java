package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);
    // List<Transaction> findByWalletInstructorId(Long instructorId);

}
