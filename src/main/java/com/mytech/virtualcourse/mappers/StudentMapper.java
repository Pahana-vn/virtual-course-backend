package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface StudentMapper {
    StudentMapper MAPPER = Mappers.getMapper(StudentMapper.class);

    @Mapping(source = "account.username", target = "username") // Map từ account
    @Mapping(source = "account.email", target = "email")
    StudentDTO studentToStudentDTO(Student student);
    @Mapping(target = "account", ignore = true) // Account được xử lý trong Service
    Student studentDTOToStudent(StudentDTO studentDTO);
}
