// src/main/java/com/mytech/virtualcourse/mappers/EnrollmentMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.EnrollmentDTO;
import com.mytech.virtualcourse.entities.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EnrollmentMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "course.id", target = "courseId")
    EnrollmentDTO toDTO(Enrollment enrollment);

    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "courseId", target = "course.id")
    Enrollment toEntity(EnrollmentDTO dto);
}
