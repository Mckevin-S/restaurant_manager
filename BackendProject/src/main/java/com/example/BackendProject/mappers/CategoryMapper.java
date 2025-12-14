package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.dto.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category entity);

    Category toEntity(CategoryDto dto);
}
