package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.CategoryDto;

import java.util.List;

public interface CategoryServiceInterface {

    List<CategoryDto> findByMenuId(Long menuId);

    List<CategoryDto> search(String keyword);

    void reorderCategories(Long menuId, List<Long> categoryIds);

    void delete(Long id);

    CategoryDto update(Long id, CategoryDto categoryDto);

    CategoryDto getById(Long id);

    List<CategoryDto> getAll();

    CategoryDto save(CategoryDto categoryDto);

}
