package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.CartDTO;
import com.mytech.virtualcourse.dtos.CartItemDTO;
import com.mytech.virtualcourse.entities.Cart;
import com.mytech.virtualcourse.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    CartDTO cartToCartDTO(Cart cart);

    List<CartItemDTO> cartItemsToCartItemDTOs(List<CartItem> cartItems);
}
