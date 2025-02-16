package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudentIdOrderByPaymentDateDesc(Long studentId);
}
