// package com.mytech.virtualcourse.mappers;

// import com.mytech.virtualcourse.dtos.BankAccountDTO;
// import com.mytech.virtualcourse.entities.BankAccount;
// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.ReportingPolicy;

// @Mapper(
//         componentModel = "spring",
//         unmappedTargetPolicy = ReportingPolicy.IGNORE
// )
// public interface BankAccountMapper {

//     @Mapping(source = "instructor.id", target = "instructorId")
//     BankAccountDTO ToDTO(BankAccount bankAccount);

//     @Mapping(target = "instructor", ignore = true) // Liên kết được xử lý trong service
//     BankAccount ToEntity(BankAccountDTO bankAccountDTO);
// }
