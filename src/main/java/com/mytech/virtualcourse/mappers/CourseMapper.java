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
                QuestionMapper.class
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CourseMapper {

    // Ánh xạ từ Entity sang DTO
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "instructor.id", target = "instructorId")
    @Mapping(source = "instructor", target = "instructorInfo")
    @Mapping(target = "instructorFirstName", source = "instructor.firstName")
    @Mapping(target = "instructorLastName", source = "instructor.lastName")
    @Mapping(target = "instructorPhoto", expression = "java(course.getInstructor() != null && course.getInstructor().getPhoto() != null ? \"http://localhost:8080/uploads/instructor/\" + course.getInstructor().getPhoto() : null)")
    @Mapping(target = "imageCover", expression = "java(course.getImageCover() != null ? \"http://localhost:8080/uploads/course/\" + course.getImageCover() : null)")
    @Mapping(target = "totalSections", ignore = true)
    @Mapping(target = "totalLectures", ignore = true)
    @Mapping(target = "totalArticles", ignore = true)
    @Mapping(target = "totalQuestions", ignore = true)
    @Mapping(target = "totalPurchasedStudents", ignore = true)
    CourseDTO courseToCourseDTO(Course course);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "level", source = "level")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "instructorPhoto", expression = "java(course.getInstructor() != null && course.getInstructor().getPhoto() != null ? \"http://localhost:8080/uploads/instructor/\" + course.getInstructor().getPhoto() : null)")
    @Mapping(target = "instructorFirstName", source = "instructor.firstName")
    @Mapping(target = "instructorLastName", source = "instructor.lastName")
    @Mapping(target = "instructorTitle", source = "instructor.title")
    @Mapping(target = "sections", source = "sections")
        CourseDetailDTO courseToCourseDetailDTO(Course course);

    // Ánh xạ từ DTO sang Entity
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "instructorId", target = "instructor.id")
    Course courseDTOToCourse(CourseDTO courseDTO);
}

