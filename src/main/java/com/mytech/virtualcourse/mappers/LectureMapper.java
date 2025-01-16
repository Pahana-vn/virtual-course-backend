package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.LectureDTO;
import com.mytech.virtualcourse.entities.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper( unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring") // Sử dụng QuestionMapper
public interface LectureMapper {
    LectureMapper INSTANCE = Mappers.getMapper(LectureMapper.class);

//    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "articles", source = "articles")
    LectureDTO lectureToLectureDTO(Lecture lecture);

//    @Mapping(target = "section.id", source = "sectionId")
    @Mapping(target = "articles", source = "articles")
    Lecture lectureDTOToLecture(LectureDTO lectureDTO);
}
