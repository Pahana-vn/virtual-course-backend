// src/main/java/com/mytech/virtualcourse/mappers/ReviewMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.ReviewDTO;
import com.mytech.virtualcourse.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewMapper {
    
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);
    
    @Mapping(source = "student.id", target = "studentId") // Map student.id to studentId
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "instructor.id", target = "instructorId") // Thêm ánh xạ này nếu cần

    ReviewDTO toDTO(Review review);
    
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    Review toEntity(ReviewDTO dto);
}
