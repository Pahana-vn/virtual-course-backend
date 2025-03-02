package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "instructor.id", target = "instructorId")
    @Mapping(source = "lastUpdated", target = "lastUpdated", qualifiedByName = "timestampToLocalDateTime")
    WalletDTO walletToWalletDTO(Wallet wallet);

    @Mapping(target = "instructor", ignore = true) // Liên kết được xử lý trong service
    @Mapping(source = "lastUpdated", target = "lastUpdated", qualifiedByName = "localDateTimeToTimestamp")
    Wallet walletDTOToWallet(WalletDTO walletDTO);

    @Named("timestampToLocalDateTime")
    default LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    @Named("localDateTimeToTimestamp")
    default Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime != null ? Timestamp.valueOf(localDateTime) : null;
    }
}