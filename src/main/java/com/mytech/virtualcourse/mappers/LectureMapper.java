package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.LectureDTO;
import com.mytech.virtualcourse.entities.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {ArticleMapper.class}
)
public interface LectureMapper {
    @Mapping(source = "articles", target = "articles")
    LectureDTO lectureToLectureDTO(Lecture lecture);
    Lecture lectureDTOToLecture(LectureDTO lectureDTO);
}
