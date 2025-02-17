package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Cart;
import com.mytech.virtualcourse.entities.CartItem;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByStudent(Student student);
}