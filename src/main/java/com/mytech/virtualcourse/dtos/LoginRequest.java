// src/main/java/com/mytech/virtualcourse/dtos/LoginRequest.java
package com.mytech.virtualcourse.dtos;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
