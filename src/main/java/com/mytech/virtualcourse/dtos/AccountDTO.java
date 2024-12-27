// src/main/java/com/mytech/virtualcourse/dtos/AccountDTO.java
package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.RoleName;
import lombok.*;

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
    private Boolean enable;
    private Boolean verifiedEmail;
    private String authenticationType;
    private String password;
    private String type;
    private String status;
    private Integer version;
    private Set<RoleName> roles;
    private Long instructorId; // Nếu có Instructor
    private Long studentId; // Nếu có Student
}
