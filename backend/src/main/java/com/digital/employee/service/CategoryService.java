package com.digital.employee.service;

import com.digital.employee.entity.Category;
import com.digital.employee.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category existing = getCategoryById(id);
        if (existing == null) return null;
        
        if (category.getCode() != null) existing.setCode(category.getCode());
        if (category.getName() != null) existing.setName(category.getName());
        if (category.getIcon() != null) existing.setIcon(category.getIcon());
        if (category.getDescription() != null) existing.setDescription(category.getDescription());
        if (category.getSortOrder() != null) existing.setSortOrder(category.getSortOrder());
        
        return categoryRepository.save(existing);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
