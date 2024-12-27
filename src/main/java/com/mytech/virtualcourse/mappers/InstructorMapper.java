// src/main/java/com/mytech/virtualcourse/mappers/InstructorMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.entities.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InstructorMapper {

    /**
     * Chuyển đổi InstructorDTO thành Instructor Entity.
     * Bỏ qua liên kết với Account vì sẽ được xử lý trong service.
     */
    @Mapping(target = "account", ignore = true)
    Instructor instructorDTOToInstructor(InstructorDTO instructorDTO);

    /**
     * Chuyển đổi Instructor Entity thành InstructorDTO.
     * Map account.id thành accountId trong DTO.
     */
//    @Mapping(target = "gender", source = "gender") // Nếu cần, có thể thêm mapping cho các trường khác

    @Mapping(target = "accountId", source = "account.id")
    InstructorDTO instructorToInstructorDTO(Instructor instructor);
}
