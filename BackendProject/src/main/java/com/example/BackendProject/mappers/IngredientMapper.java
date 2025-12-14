package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.dto.IngredientDto;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    IngredientDto toDto(Ingredient entity);

    Ingredient toEntity(IngredientDto dto);
}
