// src/main/java/com/mytech/virtualcourse/mappers/StatisticsMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.StatisticsDTO;
import com.mytech.virtualcourse.entities.Statistics;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StatisticsMapper {

    StatisticsDTO statisticsToStatisticsDTO(Statistics statistics);

    Statistics statisticsDTOToStatistics(StatisticsDTO statisticsDTO);
}
