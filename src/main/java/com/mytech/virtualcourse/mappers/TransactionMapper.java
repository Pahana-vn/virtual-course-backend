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
    @Mapping(source = "payment.id", target = "paymentId") // payment now is Payment entity
    TransactionDTO toDTO(Transaction transaction);
    
    @Mapping(target = "wallet", ignore = true) // Handle wallet in service
    @Mapping(target = "payment", ignore = true) // Handle payment in service
    Transaction toEntity(TransactionDTO dto);
}
