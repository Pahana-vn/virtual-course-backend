package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CategoryDTO;
import com.mytech.virtualcourse.entities.Category;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CategoryMapper;
import com.mytech.virtualcourse.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    // Thêm FileStorageService để xóa file cũ
    @Autowired
    private FileStorageService fileStorageService;


    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::categoryToCategoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.categoryToCategoryDTO(category);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDTO.getName() + "' already exists");
        }
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDTO(savedCategory);
    }

//    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
//        Category existingCategory = categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
//        existingCategory.setName(categoryDTO.getName());
//        existingCategory.setDescription(categoryDTO.getDescription());
//        Category updatedCategory = categoryRepository.save(existingCategory);
//        return categoryMapper.categoryToCategoryDTO(updatedCategory);
//    }
public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
    Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    // Nếu tên file cũ khác tên file mới => xóa file cũ
    // existingCategory.getImage() là file cũ
    // categoryDTO.getImage() là file mới
    if (existingCategory.getImage() != null
            && !existingCategory.getImage().isEmpty()
            && categoryDTO.getImage() != null
            && !categoryDTO.getImage().equals(existingCategory.getImage())) {

        // Xóa file cũ trong uploads/category
        fileStorageService.deleteFile(existingCategory.getImage(), "category");
    }

    // Cập nhật tên, mô tả
    existingCategory.setName(categoryDTO.getName());
    existingCategory.setDescription(categoryDTO.getDescription());

    // Cập nhật field image (nếu có)
    if (categoryDTO.getImage() != null) {
        existingCategory.setImage(categoryDTO.getImage());
    }

    // Lưu thay đổi
    Category updatedCategory = categoryRepository.save(existingCategory);
    return categoryMapper.categoryToCategoryDTO(updatedCategory);
}
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        //Nếu bạn muốn khi xóa hẳn Category (chạy deleteCategory), cũng xóa ảnh đang lưu, hãy thêm logic trong deleteCategory:
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Nếu category có image
        if (category.getImage() != null && !category.getImage().isEmpty()) {
            fileStorageService.deleteFile(category.getImage(), "category");
        }
        categoryRepository.deleteById(id);
    }
}
