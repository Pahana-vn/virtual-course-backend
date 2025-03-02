package com.mytech.virtualcourse.dtos;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangePasswordStudentDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
