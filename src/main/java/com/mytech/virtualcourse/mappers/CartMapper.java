package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.CartDTO;
import com.mytech.virtualcourse.dtos.CartItemDTO;
import com.mytech.virtualcourse.entities.Cart;
import com.mytech.virtualcourse.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

//    @Mapping(source = "studentId", target = "studentId")
    CartDTO cartToCartDTO(Cart cart);

    // Sử dụng nhiều annotation @Mapping để bỏ qua từng thuộc tính
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "instructorPhoto", ignore = true)
    @Mapping(target = "instructorFirstName", ignore = true)
    @Mapping(target = "instructorLastName", ignore = true)
    @Mapping(target = "instructorId", ignore = true)
    @Mapping(target = "progress", ignore = true)
    List<CartItemDTO> cartItemsToCartItemDTOs(List<CartItem> cartItems);
}
