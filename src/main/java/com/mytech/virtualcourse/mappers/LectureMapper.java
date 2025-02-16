package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.LectureDTO;
import com.mytech.virtualcourse.entities.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        uses = {ArticleMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LectureMapper {
    LectureMapper INSTANCE = Mappers.getMapper(LectureMapper.class);

    @Mapping(target = "sectionId", source = "section.id")
    LectureDTO lectureToLectureDTO(Lecture lecture);

    @Mapping(target = "section.id", source = "sectionId")
    Lecture lectureDTOToLecture(LectureDTO lectureDTO);
}
