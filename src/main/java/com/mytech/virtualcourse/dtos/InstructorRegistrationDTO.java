package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.Gender;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InstructorRegistrationDTO {
    // Account Info
    private String username;
    private String email;
    private String password;
    private AuthenticationType authenticationType;

    // Instructor Info
    private String firstName;
    private String lastName;
    private Gender gender;
    private String address;
    private String phone;
    private String bio;
    private String photo;
    private String title;
    private String workplace;

    // Social Info
    private String facebookUrl;
    private String googleUrl;
    private String instagramUrl;
    private String linkedinUrl;
}
