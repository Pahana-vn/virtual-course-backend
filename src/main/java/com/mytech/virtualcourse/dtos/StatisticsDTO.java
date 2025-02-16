// src/main/java/com/mytech/virtualcourse/dtos/StatisticsDTO.java

package com.mytech.virtualcourse.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatisticsDTO {
    private long accounts;
    private long instructors;
    private long students;
    private long courses;
    private long categories;
    private long roles;
}
