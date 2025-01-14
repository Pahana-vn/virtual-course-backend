// src/main/java/com/mytech/virtualcourse/dtos/AccountDTO.java
package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ERole;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
    private Long id;
    private String username;
    private String email;
    private boolean enable;
    private boolean verifiedEmail;
    private String authenticationType;
    private String password;
    private String type;
    private String status;
    private Integer version;
    private List<ERole> roles;
    // private Long instructorId; // Nếu có Instructor
    // private Long studentId; // Nếu có Student
    private String resetPasswordToken; // Thêm nếu muốn ánh xạ

}
