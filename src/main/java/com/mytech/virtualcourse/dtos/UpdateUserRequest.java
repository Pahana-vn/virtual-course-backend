// src/main/java/com/mytech/virtualcourse/dtos/UpdateUserRequest.java
package com.mytech.virtualcourse.dtos;

import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateUserRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;
//
//    private String type;
    private String status;
}
