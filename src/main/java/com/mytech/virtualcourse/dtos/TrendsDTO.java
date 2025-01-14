// src/main/java/com/mytech/virtualcourse/dtos/TrendsDTO.java

package com.mytech.virtualcourse.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrendsDTO {
    private List<String> dates; // e.g., ["Jan", "Feb", ...]
    private List<Long> accounts;
    private List<Long> instructors;
    private List<Long> students;
    private List<Long> courses;
    private List<Long> categories;
    private List<Long> roles;
}
