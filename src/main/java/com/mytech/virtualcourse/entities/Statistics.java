// src/main/java/com/mytech/virtualcourse/models/Statistics.java

package com.mytech.virtualcourse.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Statistics {

    private long accounts;
    private long instructors;
    private long students;
    private long courses;
    private long categories;
    private long roles;
}
