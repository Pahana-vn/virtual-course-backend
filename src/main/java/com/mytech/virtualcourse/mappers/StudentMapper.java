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

    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.email", target = "email")
    @Mapping(target = "firstName", expression = "java(student.getFirstName() != null ? student.getFirstName() : \"\")")
    @Mapping(target = "lastName", expression = "java(student.getLastName() != null ? student.getLastName() : \"\")")
    @Mapping(target = "dob", expression = "java(student.getDob() != null ? student.getDob() : new java.util.Date())")
    @Mapping(target = "address", expression = "java(student.getAddress() != null ? student.getAddress() : \"\")")
    @Mapping(target = "gender", expression = "java(student.getGender() != null ? student.getGender().name() : \"OTHER\")")
    @Mapping(target = "phone", expression = "java(student.getPhone() != null ? student.getPhone() : \"\")")
    @Mapping(target = "avatar", expression = "java(student.getAvatar() != null ? student.getAvatar() : \"default-avatar.png\")")
    @Mapping(target = "bio", expression = "java(student.getBio() != null ? student.getBio() : \"\")")

    StudentDTO studentToStudentDTO(Student student);

    Student studentDTOToStudent(StudentDTO studentDTO);
}
