package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.WalletBalanceDTO;
import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WalletMapper {
    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "instructor.id", target = "instructorId")
    WalletDTO walletToWalletDTO(Wallet wallet);

    WalletBalanceDTO toWalletBalanceDTO(Wallet wallet);

    @Mapping(target = "instructor", ignore = true)
    Wallet walletDTOToWallet(WalletDTO walletDTO);
}
