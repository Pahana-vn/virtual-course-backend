package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.mytech.virtualcourse.entities.RefreshToken;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(Account user);
    @Modifying
    void deleteByUser(Account user);
}
