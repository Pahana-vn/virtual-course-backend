package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.entities.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InstructorMapper {
    InstructorMapper MAPPER = Mappers.getMapper(InstructorMapper.class);

    InstructorDTO instructorToInstructorDTO(Instructor instructor);

    Instructor instructorDTOToInstructor(InstructorDTO instructorDTO);
}
