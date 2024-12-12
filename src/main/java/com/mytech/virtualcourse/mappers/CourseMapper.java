package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.CourseDetailDTO;
import com.mytech.virtualcourse.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {SectionMapper.class}
)
public interface CourseMapper {
    @Mapping(target = "instructorFirstName", source = "instructor.firstName")
    @Mapping(target = "instructorLastName", source = "instructor.lastName")
    @Mapping(
            target = "instructorPhoto",
            expression = "java(course.getInstructor() != null && course.getInstructor().getPhoto() != null ? \"http://localhost:8080/uploads/instructor/\" + course.getInstructor().getPhoto() : null)"
    )
    @Mapping(
            target = "imageCover",
            expression = "java(course.getImageCover() != null ? \"http://localhost:8080/uploads/course/\" + course.getImageCover() : null)"
    )
    CourseDTO courseToCourseDTO(Course course);

    @Mapping(target = "sections", source = "sections")
    CourseDetailDTO courseToCourseDetailDTO(Course course);

    Course courseDTOToCourse(CourseDTO courseDTO);
}
