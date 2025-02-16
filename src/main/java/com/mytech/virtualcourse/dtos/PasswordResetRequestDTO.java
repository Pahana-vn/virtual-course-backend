package com.mytech.virtualcourse.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDTO {
    private String email;
}
