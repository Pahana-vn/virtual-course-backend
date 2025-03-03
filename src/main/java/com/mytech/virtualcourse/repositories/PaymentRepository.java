package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaypalPaymentId(String paypalPaymentId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(DISTINCT e.student.id) FROM Payment e JOIN e.courses c WHERE c.instructor.id = :instructorId")
    int countDistinctStudentsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.courses c WHERE c.instructor.id = :instructorId AND p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'COMPLETED'")
    BigDecimal getRevenueForInstructorInPeriod(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT p.student.id) FROM Payment p JOIN p.courses c WHERE c.instructor.id = :instructorId AND p.status = 'COMPLETED'")
    Long countActiveStudentsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(p) FROM Payment p JOIN p.courses c WHERE c.instructor.id = :instructorId AND p.status = 'Completed'")
    Long countCompletedEnrollmentsByInstructorId(@Param("instructorId") Long instructorId);
    @Query("SELECT COUNT(p) FROM Payment p JOIN p.courses c WHERE c.instructor.id = :instructorId")
    Long countEnrollmentsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.courses c WHERE c.instructor.id = :instructorId AND p.status = 'COMPLETED'")
    BigDecimal getTotalRevenueForInstructor(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(p) FROM Payment p JOIN p.courses c WHERE c.instructor.id = :instructorId AND p.paymentDate BETWEEN :startDate AND :endDate")
    Long countEnrollmentsByInstructorIdAndDateRange(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}