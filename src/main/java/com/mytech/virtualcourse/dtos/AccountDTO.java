package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.enums.EAccountStatus;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
    private int id;
    private String username;
    private String email;
//    private Boolean enable;
//    private Boolean verifiedEmail;
//    private String password;
//    private String type;
    private EAccountStatus status;
//    private Integer version;
//    private String authenticationType;
//    private List<RoleDTO> roles;
}
