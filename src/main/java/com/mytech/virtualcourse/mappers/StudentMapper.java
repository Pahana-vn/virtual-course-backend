// src/main/java/com/mytech/virtualcourse/mappers/StudentMapper.java
package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StudentMapper {

    @Named("studentToStudentDTO")
    @Mapping(target = "accountId", source = "account.id") // Thêm mapping cho accountId
    StudentDTO studentToStudentDTO(Student student);

    @Named("studentDTOToStudent")
    @Mapping(target = "account", ignore = true) // Bỏ qua khi ánh xạ, vì đã xử lý trong Service
    Student studentDTOToStudent(StudentDTO dto);
}
