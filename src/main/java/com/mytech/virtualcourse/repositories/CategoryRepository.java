package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    boolean existsByName(String name);
    @Query("SELECT MIN(a.createdAt) FROM Account a")
    LocalDate getFirstCreatedDate();
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end); // Changed to LocalDateTime
}
