package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InstructorProfileDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean verifiedEmail;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String phone;
    private String bio;
    private String title;
    private String workplace;
    private String photo;
    private Boolean verifiedPhone;
}
