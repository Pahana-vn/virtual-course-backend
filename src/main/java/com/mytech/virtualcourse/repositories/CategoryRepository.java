package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    boolean existsByName(String name);

    long countByCreatedAtBetween(Date start, Date end);
}
