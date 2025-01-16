package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.ERole;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateAccountDTO {
    private String username;
    private String email;
    private String password; // Thêm trường password
    private EAccountStatus status;
    private boolean verifiedEmail;
    private AuthenticationType authenticationType;
    private Integer version;
    private List<ERole> roles;
}
