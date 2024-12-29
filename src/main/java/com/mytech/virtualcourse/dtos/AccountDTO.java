package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
    private String username;
    private String email;
    private Boolean enable;
    private Boolean verifiedEmail;
    private Integer version;
    private String authenticationType;
    private List<NotificationDTO> notifications; // DTO cho Notification
    private List<RoleDTO> roles; // DTO cho Role
}
