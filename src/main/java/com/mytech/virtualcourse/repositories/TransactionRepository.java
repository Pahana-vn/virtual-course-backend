package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.enums.TransactionType;
import com.mytech.virtualcourse.enums.StatusTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    List<Transaction> findByWalletId(Long walletId);

    Page<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    List<Transaction> findByWalletIdAndStatusTransactionOrderByCreatedAtDesc(Long walletId, StatusTransaction status);
    // Add these methods to your existing TransactionRepository interface

    Page<Transaction> findByTransactionTypeAndStatusTransaction(
            TransactionType transactionType,
            StatusTransaction statusTransaction,
            Pageable pageable);

    Page<Transaction> findByTransactionType(TransactionType transactionType, Pageable pageable);

    Page<Transaction> findByStatusTransaction(StatusTransaction statusTransaction, Pageable pageable);

    long countByTransactionType(TransactionType transactionType);

    long countByStatusTransaction(StatusTransaction statusTransaction);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.transactionType = :transactionType")
    BigDecimal sumAmountByTransactionType(TransactionType transactionType);

    // Trong TransactionRepository
    @Query(value = "SELECT DATE_FORMAT(created_at, '%b') AS month, " +
            "YEAR(created_at) AS year, " +
            "SUM(CASE WHEN transaction_type = 'DEPOSIT' AND status_transaction = 'COMPLETED' THEN amount ELSE 0 END) AS deposits, " +
            "SUM(CASE WHEN transaction_type = 'WITHDRAWAL' AND status_transaction = 'COMPLETED' THEN amount ELSE 0 END) AS withdrawals, " +
            "COUNT(CASE WHEN transaction_type = 'DEPOSIT' THEN 1 ELSE NULL END) AS deposit_count, " +
            "COUNT(CASE WHEN transaction_type = 'WITHDRAWAL' THEN 1 ELSE NULL END) AS withdrawal_count " +
            "FROM transaction " +
            "WHERE created_at >= :startDate " +
            "GROUP BY DATE_FORMAT(created_at, '%b'), YEAR(created_at)",
            nativeQuery = true)
    List<Object[]> findMonthlyTransactionTrends(@Param("startDate") LocalDateTime startDate);
}
