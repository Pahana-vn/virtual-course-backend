package com.mytech.virtualcourse.dtos;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtDTO {
    private String token;
    private Long accountId;
    private Long studentId;
    private Long instructorId;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
}
