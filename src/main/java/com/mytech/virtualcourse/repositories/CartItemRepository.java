package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Cart;
import com.mytech.virtualcourse.entities.CartItem;
import com.mytech.virtualcourse.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndCourse(Cart cart, Course course);
    List<CartItem> findByCart(Cart cart);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart = :cart")
    void deleteByCart(Cart cart);
}