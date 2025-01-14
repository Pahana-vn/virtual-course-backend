// src/main/java/com/mytech/virtualcourse/mappers/InstructorMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.entities.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InstructorMapper {

    InstructorMapper INSTANCE = Mappers.getMapper(InstructorMapper.class);

    /**
     * Chuyển đổi InstructorDTO thành Instructor Entity.
     * Bỏ qua liên kết với Account vì sẽ được xử lý trong service.
     */
    @Mapping(target = "account", ignore = true)
    @Mapping(source = "walletId", target = "wallet.id") // Sử dụng walletId thay vì wallet.id
    @Mapping(target = "account.status",ignore = true)
    Instructor instructorDTOToInstructor(InstructorDTO instructorDTO);

    /**
     * Chuyển đổi Instructor Entity thành InstructorDTO.
     * Map account.id thành accountId trong DTO.
     */
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "status", source = "account.status")
    InstructorDTO instructorToInstructorDTO(Instructor instructor);
}
