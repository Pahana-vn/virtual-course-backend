// src/main/java/com/mytech/virtualcourse/dtos/ResetPasswordRequest.java
package com.mytech.virtualcourse.dtos;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResetPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    private String newPassword;
}
