// src/main/java/com/mytech/virtualcourse/controllers/StatisticsController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.StatisticsDTO;
import com.mytech.virtualcourse.dtos.TrendsDTO;
import com.mytech.virtualcourse.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public StatisticsDTO getStatistics(@RequestParam(defaultValue = "all") String filter) {
        return statisticsService.getStatistics(filter);
    }

    @GetMapping("/trends")
    public TrendsDTO getTrends(@RequestParam(defaultValue = "all") String filter) {
        return statisticsService.getTrends(filter);
    }
}
