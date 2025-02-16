package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {


    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(DISTINCT e.student.id) FROM Payment e JOIN e.courses c WHERE c.instructor.id = :instructorId")
    int countDistinctStudentsByInstructorId(@Param("instructorId") Long instructorId);
}