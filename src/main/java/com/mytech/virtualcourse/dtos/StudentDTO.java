package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Date dob;
    private String address;
    private String gender;
    private String phone;
    private String avatar;
    private String bio;
    private Boolean verifiedPhone;
    private String categoryPrefer;
    private String statusStudent;
    private String username;
    private String email;
}
