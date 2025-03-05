package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Wallet;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByInstructorId(Long instructorId);
}