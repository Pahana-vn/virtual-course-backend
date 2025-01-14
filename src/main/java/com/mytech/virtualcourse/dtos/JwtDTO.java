package com.mytech.virtualcourse.dtos;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtDTO {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
}
