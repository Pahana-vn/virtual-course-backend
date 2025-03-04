package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;
    private String avatar;
    private String bio;
    private Boolean verifiedPhone;
    private String categoryPrefer;
    private String statusStudent;
    @NotBlank(message = "Username is required")
    private String username;
    private String email;
}
