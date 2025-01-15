package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.ERole;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAccountDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private EAccountStatus status;
    private boolean verifiedEmail;
    private Integer version;
    private String authenticationType;
    private List<ERole> roles;

    // Các trường bổ sung dành cho Admin nếu cần
}
