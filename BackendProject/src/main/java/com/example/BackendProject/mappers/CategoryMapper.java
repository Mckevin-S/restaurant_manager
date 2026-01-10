package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.dto.CategoryDto;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "menuId", source = "menu.id")
    CategoryDto toDto(Category category);

    @Mapping(target = "menu.id", source = "menuId")
    @Mapping(target = "plats", ignore = true)
    Category toEntity(CategoryDto categoryDto);

    // AJOUTEZ CETTE MÉTHODE pour expliquer à MapStruct comment gérer l'Optional
    default <T> T unwrap(Optional<T> optional) {
        return optional.orElse(null);
    }

}
