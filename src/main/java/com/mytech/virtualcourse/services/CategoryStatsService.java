package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CategoryDTO;
import com.mytech.virtualcourse.dtos.CategoryWithStatsDTO;
import com.mytech.virtualcourse.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryStatsService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Enriches a CategoryDTO with course count information
     */
    public CategoryWithStatsDTO enrichCategoryWithStats(CategoryDTO category) {
        int courseCount = courseRepository.countByCategoryId(category.getId());
        return new CategoryWithStatsDTO(category, courseCount);
    }

    /**
     * Enriches a list of CategoryDTOs with course count information
     */
    public List<CategoryWithStatsDTO> enrichCategoriesWithStats(List<CategoryDTO> categories) {
        return categories.stream()
                .map(this::enrichCategoryWithStats)
                .collect(Collectors.toList());
    }

    // Add this method to CategoryStatsService
    public List<CategoryWithStatsDTO> enrichCategoriesWithStatsOptimized(List<CategoryDTO> categories) {
        // Get all course counts in a single query
        List<Object[]> courseCounts = courseRepository.countCoursesGroupByCategory();

        // Convert to a map for easy lookup
        Map<Long, Integer> categoryIdToCountMap = new HashMap<>();
        for (Object[] result : courseCounts) {
            Long categoryId = (Long) result[0];
            Integer count = ((Number) result[1]).intValue();
            categoryIdToCountMap.put(categoryId, count);
        }

        // Create DTOs with the counts
        return categories.stream()
                .map(category -> {
                    int count = categoryIdToCountMap.getOrDefault(category.getId(), 0);
                    return new CategoryWithStatsDTO(category, count);
                })
                .collect(Collectors.toList());
    }

}
