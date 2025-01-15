package com.mytech.virtualcourse.dtos;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {
    private String token;
    private String newPassword;
}