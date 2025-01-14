package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CategoryDTO;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Lấy tất cả categories.
     * Trả về list CategoryDTO, trong đó field 'image' chỉ chứa tên file,
     * ví dụ "1734854100323_Picture5.png". Không thêm "/uploads/category/" ở đây.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Nếu bạn muốn getListCategory cũng cần ADMIN
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Lấy category theo ID.
     * Kiểm tra category = null thì ném ResourceNotFoundException.
     * Không thêm "/uploads/category/" vào field 'image'.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Nếu bạn muốn getListCategory cũng cần ADMIN
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        // Chỉ trả về categoryDTO, image là tên file gốc (nếu có).
        return ResponseEntity.ok(category);
    }

    /**
     * Tạo mới category.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Cập nhật category.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Xóa category.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
