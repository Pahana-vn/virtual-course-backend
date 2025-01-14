// src/main/java/com/mytech/virtualcourse/dtos/RegisterRequest.java
package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.ERole;
import lombok.*;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    private String password;

    @NotEmpty(message = "Chọn ít nhất một vai trò")
    private List<ERole> roles;
}
