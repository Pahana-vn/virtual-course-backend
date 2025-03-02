package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.StatisticsDTO;
import com.mytech.virtualcourse.dtos.TrendsDTO;
import com.mytech.virtualcourse.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/statistics")  // Change from /api/statistics to /api/admin/statistics
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // Fetch statistics based on filter (today, week, month, etc.)
    @GetMapping
    public StatisticsDTO getStatistics(@RequestParam(defaultValue = "allTime") String filter,
                                       @RequestParam String model) {
        return statisticsService.getStatistics(filter, model);
    }

    // Fetch trends based on filter
    @GetMapping("/trends")
    public TrendsDTO getTrends(@RequestParam(defaultValue = "allTime") String filter) {
        return statisticsService.getTrends(filter);
    }
}
