package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    Account findByEmail(String email);
}
