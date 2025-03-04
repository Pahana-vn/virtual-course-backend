package com.mytech.virtualcourse.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithStatsDTO {
    private CategoryDTO category;
    private int courseCount;


}
