package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialDTO {
    private Long id;
    private Long instructorId;
    private String facebookUrl;
    private String googleUrl;
    private String instagramUrl;
    private String linkedinUrl;
}
