package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.SectionDTO;
import com.mytech.virtualcourse.entities.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {LectureMapper.class}
)
public interface SectionMapper {
    @Mapping(source = "lectures", target = "lectures")
    SectionDTO sectionToSectionDTO(Section section);

    Section sectionDTOToSection(SectionDTO sectionDTO);
}
