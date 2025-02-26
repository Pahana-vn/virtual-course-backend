package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.InstructorRegistrationDTO;
import com.mytech.virtualcourse.entities.Social;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SocialMapper {

    @Mapping(target = "instructor", ignore = true)
    Social instructorRegistrationDTToSocial(InstructorRegistrationDTO instructorRegistrationDTO);
}
