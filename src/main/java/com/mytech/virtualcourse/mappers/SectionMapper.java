package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.SectionDTO;
import com.mytech.virtualcourse.entities.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {LectureMapper.class},unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring") // Sử dụng LectureMapper
public interface SectionMapper {
    SectionMapper INSTANCE = Mappers.getMapper(SectionMapper.class);

//    @Mapping(target = "courseId", source = "course.id") // Map lectures từ Section sang LectureDTO
    @Mapping(target = "lectures", source = "lectures") // Map lectures từ Section sang LectureDTO
    SectionDTO sectionToSectionDTO(Section section);

//    @Mapping(target = "course.id", source = "courseId") // Map lectures từ Section sang LectureDTO
    @Mapping(target = "lectures", source = "lectures")
    Section sectionDTOToSection(SectionDTO sectionDTO);
}
