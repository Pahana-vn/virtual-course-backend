package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CategoryDTO;
import com.mytech.virtualcourse.dtos.CategoryWithStatsDTO;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.services.CategoryService;
import com.mytech.virtualcourse.services.CategoryStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryStatsService categoryStatsService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(@RequestParam(required = false) String platform) {
        List<CategoryDTO> categories = categoryService.getAllCategories(platform);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        if (category.getImage() != null) {
            category.setImage("/uploads/category/" + category.getImage());
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoint that returns CategoryWithStatsDTO
    @GetMapping("/with-stats")
    public ResponseEntity<List<CategoryWithStatsDTO>> getAllCategoriesWithStats() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        List<CategoryWithStatsDTO> categoriesWithStats = categoryStatsService.enrichCategoriesWithStats(categories);
        return ResponseEntity.ok(categoriesWithStats);
    }

    // Get a single category with stats
    @GetMapping("/{id}/with-stats")
    public ResponseEntity<CategoryWithStatsDTO> getCategoryWithStats(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        CategoryWithStatsDTO categoryWithStats = categoryStatsService.enrichCategoryWithStats(category);
        return ResponseEntity.ok(categoryWithStats);
    }


}
