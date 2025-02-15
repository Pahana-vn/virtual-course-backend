// src/main/java/com/mytech/virtualcourse/models/Trends.java

package com.mytech.virtualcourse.entities;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trends {
    private List<String> dates; // e.g., ["Jan", "Feb", ...]
    private List<Long> accounts;
    private List<Long> instructors;
    private List<Long> students;
    private List<Long> courses;
    private List<Long> categories;
    private List<Long> roles;
}
