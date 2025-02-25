package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWallet_InstructorId(Long instructorId);

    List<Transaction> findByWallet_InstructorIdAndTransactionType(Long instructorId, TransactionType transactionType);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.wallet.instructor.id = :instructorId")
    int countTransactionsByInstructorId(Long instructorId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionType = 'DEPOSIT' " +
            "AND t.wallet.instructor.id = :instructorId")
    int countDepositsInTransactionsByInstructorId(Long instructorId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionType = 'WITHDRAWAL' " +
            "AND t.wallet.instructor.id = :instructorId")
    int countWithdrawalsInTransactionsByInstructorId(Long instructorId);
}
