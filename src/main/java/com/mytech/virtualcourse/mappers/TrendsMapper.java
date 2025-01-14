// src/main/java/com/mytech/virtualcourse/mappers/TrendsMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.TrendsDTO;
import com.mytech.virtualcourse.entities.Trends;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TrendsMapper {

    TrendsDTO trendsToTrendsDTO(Trends trends);

    Trends trendsDTOToTrends(TrendsDTO trendsDTO);
}
