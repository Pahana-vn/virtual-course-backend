package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.InstructorProfileDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mappings({
            @Mapping(source = "account.username", target = "username"),
            @Mapping(source = "account.email", target = "email"),
            @Mapping(source = "account.verifiedEmail", target = "verifiedEmail"),
            @Mapping(source = "instructor.firstName", target = "firstName"),
            @Mapping(source = "instructor.lastName", target = "lastName"),
            @Mapping(source = "instructor.gender", target = "gender"),
            @Mapping(source = "instructor.address", target = "address"),
            @Mapping(source = "instructor.phone", target = "phone"),
            @Mapping(source = "instructor.bio", target = "bio"),
            @Mapping(source = "instructor.title", target = "title"),
            @Mapping(source = "instructor.workplace", target = "workplace"),
            @Mapping(source = "instructor.photo", target = "photo")
    })
    InstructorProfileDTO EntitiestoInstructorProfileDTO(Account account, Instructor instructor);
}
