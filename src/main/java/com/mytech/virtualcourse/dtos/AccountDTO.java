package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.EAccountStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String username;
    private String email;
    private EAccountStatus status;
}
