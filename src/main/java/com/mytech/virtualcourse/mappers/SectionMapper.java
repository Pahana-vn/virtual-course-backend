package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.SectionDTO;
import com.mytech.virtualcourse.entities.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        uses = {LectureMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SectionMapper {
    SectionMapper INSTANCE = Mappers.getMapper(SectionMapper.class);

    @Mapping(target = "courseId", source = "course.id")
    SectionDTO sectionToSectionDTO(Section section);

    @Mapping(target = "course.id", source = "courseId")
    Section sectionDTOToSection(SectionDTO sectionDTO);
}
