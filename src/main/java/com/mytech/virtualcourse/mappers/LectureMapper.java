package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.LectureDTO;
import com.mytech.virtualcourse.entities.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface LectureMapper {
    LectureDTO lectureToLectureDTO(Lecture lecture);
    Lecture lectureDTOToLecture(LectureDTO lectureDTO);
}
