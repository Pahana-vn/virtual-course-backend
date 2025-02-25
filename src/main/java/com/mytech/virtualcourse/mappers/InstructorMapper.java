package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.enums.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InstructorMapper {
    InstructorMapper MAPPER = Mappers.getMapper(InstructorMapper.class);

    @Mapping(target = "accountUsername", source = "account.username")
    @Mapping(target = "accountEmail", source = "account.email")
    @Mapping(target = "gender", expression = "java(instructor.getGender() != null ? instructor.getGender().name() : null)")
    InstructorDTO instructorToInstructorDTO(Instructor instructor);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "gender", expression = "java(genderFromString(instructorDTO.getGender()))")
    Instructor instructorDTOToInstructor(InstructorDTO instructorDTO);

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "instructorName", expression = "java(instructor.getFirstName() + ' ' + instructor.getLastName())")
    @Mapping(target = "totalCourses", source = "totalCourses")
    @Mapping(target = "totalPublishedCourses", source = "totalPublishedCourses")
    @Mapping(target = "totalPendingCourses", source = "totalPendingCourses")
    @Mapping(target = "totalStudents", source = "totalStudents")
    @Mapping(target = "totalPurchasedCourses", source = "totalPurchasedCourses")
    @Mapping(target = "totalTransactions", source = "totalTransactions")
    @Mapping(target = "totalDeposits", source = "totalDeposits")
    @Mapping(target = "totalWithdrawals", source = "totalWithdrawals")
//    @Mapping(target = "totalReviews", source = "totalReviews")
    @Mapping(target = "avatarImage", expression = "java(instructor.getPhoto() != null ? \"http://localhost:8080/uploads/instructor/\" + instructor.getPhoto() : null)")
    @Mapping(target = "balance", source = "instructor.wallet.balance")
    InstructorStatisticsDTO toInstructorStatisticsDTO(Instructor instructor, int totalCourses, int totalPublishedCourses, int totalPendingCourses, int totalStudents, int totalPurchasedCourses, int totalTransactions, int totalDeposits, int totalWithdrawals, BigDecimal balance);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "photo", source = "photo")
    InstructorInfo instructorToInstructorInfo(Instructor instructor);

    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "photo", expression = "java(instructor.getPhoto() != null ? \"http://localhost:8080/uploads/instructor/\" + instructor.getPhoto() : null)")
    @Mapping(target = "verifiedEmail", source = "account.verifiedEmail")
    InstructorProfileDTO instructorToInstructorProfileDTO(Instructor instructor);

    @Mapping(target = "id", source = "instructor.id")
    @Mapping(target = "firstName", source = "instructor.firstName")
    @Mapping(target = "lastName", source = "instructor.lastName")
    @Mapping(target = "title", source = "instructor.title")
    @Mapping(target = "address", source = "instructor.address")
    @Mapping(target = "phone", source = "instructor.phone")
    @Mapping(target = "photo", expression = "java(instructor.getPhoto() != null ? \"http://localhost:8080/uploads/instructor/\" + instructor.getPhoto() : null)")
    @Mapping(target = "bio", source = "instructor.bio")
    @Mapping(target = "totalCourses", source = "totalCourses")
    @Mapping(target = "totalSections", source = "totalSections")
    @Mapping(target = "totalStudents", source = "totalStudents")
    @Mapping(target = "averageRating", source = "averageRating")
    @Mapping(target = "education", source = "instructor.education")
    @Mapping(target = "experiences", source = "instructor.experiences")
    @Mapping(target = "skills", source = "instructor.skills")
    @Mapping(target = "social", source = "instructor.social")
    InstructorDetailsDTO instructorToInstructorDetailsDTO(Instructor instructor, int totalCourses, int totalSections, int totalStudents, double averageRating);

    @Mapping(target = "account", ignore = true)
    Instructor instructorProfileDTOToInstructor(InstructorProfileDTO profileDTO);

    // Chuyển đổi từ String sang Gender (Enum)
    default Gender genderFromString(String gender) {
        return gender != null ? Gender.valueOf(gender.toUpperCase()) : null;
    }
}
