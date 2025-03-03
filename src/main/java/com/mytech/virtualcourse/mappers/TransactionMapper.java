package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    
    @Mapping(source = "wallet.id", target = "walletId")
    @Mapping(source = "walletBalance", target = "walletBalance")
    TransactionDTO toDTO(Transaction transaction);
    
    @Mapping(target = "wallet", ignore = true)
    Transaction toEntity(TransactionDTO dto);
}
