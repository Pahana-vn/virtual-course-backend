// src/main/java/com/mytech/virtualcourse/mappers/TransactionMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    
    @Mapping(source = "wallet.id", target = "walletId")
    @Mapping(source = "payment.id", target = "paymentId")
    TransactionDTO toDTO(Transaction transaction);
    
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "payment", ignore = true)
    Transaction toEntity(TransactionDTO dto);
}
