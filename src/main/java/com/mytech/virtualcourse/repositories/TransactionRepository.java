package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.enums.StatusTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);

    Page<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    List<Transaction> findByWalletIdAndStatusTransactionOrderByCreatedAtDesc(Long walletId, StatusTransaction status);


}
