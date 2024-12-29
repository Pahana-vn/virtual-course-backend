// src/main/java/com/mytech/virtualcourse/dtos/ForgotPasswordRequest.java
package com.mytech.virtualcourse.dtos;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;
}
