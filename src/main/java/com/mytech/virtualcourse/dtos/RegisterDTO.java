package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String username;
    private String email;
    private String password;
    private String role;
}
