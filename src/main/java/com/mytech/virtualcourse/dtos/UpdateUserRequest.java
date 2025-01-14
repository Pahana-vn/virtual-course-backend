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
public class UpdateUserRequest {
    private String username;
    private String email;
    private EAccountStatus status;
    private boolean enable;
    private boolean verifiedEmail;
    private String authenticationType;
    private String type;
    private Integer version;
    private List<ERole> roles;
}
