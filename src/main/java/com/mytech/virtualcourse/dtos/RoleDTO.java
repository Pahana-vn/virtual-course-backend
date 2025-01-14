// src/main/java/com/mytech/virtualcourse/dtos/RoleDTO.java
package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.ERole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleDTO {
    private Long id;
    private ERole name;
    private String description;
}
