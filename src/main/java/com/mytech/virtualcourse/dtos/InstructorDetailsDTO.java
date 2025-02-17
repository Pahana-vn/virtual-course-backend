package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InstructorDetailsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String photo;
    private String address;
    private String phone;
    private String bio;
    private int totalCourses;
    private int totalSections;
    private int totalStudents;
    private double averageRating;
    private List<EducationDTO> education;
    private List<ExperienceDTO> experiences;
    private List<SkillDTO> skills;
    private SocialDTO social;
}
