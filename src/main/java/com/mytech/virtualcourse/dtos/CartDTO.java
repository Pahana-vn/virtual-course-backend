package com.mytech.virtualcourse.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private Long studentId;
    private List<CartItemDTO> cartItems;
}
