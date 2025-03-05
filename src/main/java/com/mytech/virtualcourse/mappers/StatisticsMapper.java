// src/main/java/com/mytech/virtualcourse/mappers/StatisticsMapper.java

package com.mytech.virtualcourse.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StatisticsMapper {

}
