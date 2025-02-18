package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstructorProfileMapper {
    InstructorProfileMapper INSTANCE = Mappers.getMapper(InstructorProfileMapper.class);

    // Education Mapping
    EducationDTO toEducationDTO(Education education);
    Education toEducationEntity(EducationDTO dto);
    List<EducationDTO> toEducationDTOList(List<Education> educations);

    // Experience Mapping
    ExperienceDTO toExperienceDTO(Experience experience);
    Experience toExperienceEntity(ExperienceDTO dto);
    List<ExperienceDTO> toExperienceDTOList(List<Experience> experiences);

    // Skill Mapping
    SkillDTO toSkillDTO(Skill skill);
    Skill toSkillEntity(SkillDTO dto);
    List<SkillDTO> toSkillDTOList(List<Skill> skills);

    // Social Mapping
    SocialDTO toSocialDTO(Social social);
    Social toSocialEntity(SocialDTO dto);
}
