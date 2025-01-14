// src/main/java/com/mytech/virtualcourse/mappers/WalletMapper.java

package com.mytech.virtualcourse.mappers;

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
    @Mapping(source = "student.id", target = "studentId")
    WalletDTO walletToWalletDTO(Wallet wallet);

    @Mapping(target = "instructor", ignore = true) // Liên kết được xử lý trong service
    @Mapping(target = "student", ignore = true)
    Wallet walletDTOToWallet(WalletDTO walletDTO);
}
