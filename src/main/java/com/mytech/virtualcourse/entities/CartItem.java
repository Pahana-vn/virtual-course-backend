package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_item")
public class CartItem extends AbstractEntity {

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Số lượng (nếu áp dụng)

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart; // Giỏ hàng mà mục này thuộc về

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Khóa học mà mục này đại diện


}
