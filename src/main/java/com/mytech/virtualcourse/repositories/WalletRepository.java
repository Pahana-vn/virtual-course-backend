package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // This method is causing the error because Wallet doesn't have a studentId property
    // Remove this method or replace it with a valid one
    // Optional<Wallet> findByStudentId(Long studentId);

    // If you need to find by instructor ID instead:
    Optional<Wallet> findByInstructorId(Long instructorId);
}