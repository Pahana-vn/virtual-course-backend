package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.enums.ERole;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleDTO {

    private ERole name;
    private String description;
}
