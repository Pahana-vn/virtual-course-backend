package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.CourseDetailDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Test;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                InstructorMapper.class,
                CategoryMapper.class,
                SectionMapper.class,
                TestMapper.class,
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CourseMapper {

    // Ánh xạ từ Entity sang DTO
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "instructor.id", target = "instructorId")
    @Mapping(source = "instructor", target = "instructorInfo") // Sử dụng InstructorMapper
    @Mapping(source = "sections", target = "sections")
    @Mapping(source = "questions", target = "questions")
    @Mapping(target = "instructorFirstName", source = "instructor.firstName")
    @Mapping(target = "instructorLastName", source = "instructor.lastName")
    @Mapping(target = "instructorPhoto", expression = "java(course.getInstructor() != null && course.getInstructor().getPhoto() != null ? \"http://localhost:8080/uploads/instructor/\" + course.getInstructor().getPhoto() : null)")
    @Mapping(target = "imageCover", expression = "java(course.getImageCover() != null ? \"http://localhost:8080/uploads/course/\" + course.getImageCover() : null)")
    CourseDTO courseToCourseDTO(Course course);

    // Ánh xạ từ DTO sang Entity
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "instructorId", target = "instructor.id")
    @Mapping(source = "sections", target = "sections") // Ánh xạ danh sách sections
    @Mapping(source = "questions", target = "questions")
    Course courseDTOToCourse(CourseDTO courseDTO);

    @Mapping(target = "sections", source = "sections")
    CourseDetailDTO courseToCourseDetailDTO(Course course);
}
