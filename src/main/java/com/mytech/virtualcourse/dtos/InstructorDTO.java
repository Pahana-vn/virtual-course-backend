package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InstructorDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String phone;
    private Boolean verifiedPhone;
    private String bio;
    private String title;
    private String photo;
    private String workplace;
    private String accountUsername;
}