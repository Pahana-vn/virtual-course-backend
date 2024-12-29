package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.dtos.InstructorInfo;
import com.mytech.virtualcourse.dtos.InstructorStatisticsDTO;
import com.mytech.virtualcourse.entities.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InstructorMapper {
    InstructorMapper MAPPER = Mappers.getMapper(InstructorMapper.class);

    InstructorDTO instructorToInstructorDTO(Instructor instructor);

    Instructor instructorDTOToInstructor(InstructorDTO instructorDTO);

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "instructorName", expression = "java(instructor.getFirstName() + ' ' + instructor.getLastName())")
    @Mapping(target = "totalCourses", source = "totalCourses")
    @Mapping(target = "totalStudents", source = "totalStudents")
    @Mapping(target = "avatarImage", source = "instructor.photo")
    @Mapping(target = "balance", source = "instructor.wallet.balance")
    InstructorStatisticsDTO toInstructorStatisticsDTO(Instructor instructor, Long totalCourses, Long totalStudents, BigDecimal balance);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "photo", source = "photo")
    InstructorInfo instructorToInstructorInfo(Instructor instructor);
}
