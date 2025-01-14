package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {


    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidByStudentId(@Param("studentId") Long studentId);
    // Thêm phương thức để lấy danh sách Payment theo studentId
    @Query("SELECT p FROM Payment p WHERE p.student.account.id = :userId")
    List<Payment> findByUserId(@Param("userId") Long userId);}