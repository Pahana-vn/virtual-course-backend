package com.mytech.virtualcourse.dtos;
import lombok.*;
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