// src/main/java/com/mytech/virtualcourse/dtos/JwtResponse.java
package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }
}
