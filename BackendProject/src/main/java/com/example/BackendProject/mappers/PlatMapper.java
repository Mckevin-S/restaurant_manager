package com.example.BackendProject.mappers;

import com.example.BackendProject.entities.Category;
import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.dto.PlatDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatMapper {

    // Entité (Plat) -> DTO (PlatDto)
    // On prend l'ID de l'objet category de l'entité pour le mettre dans le Long category du DTO
    @Mapping(target = "category", source = "entity.category.id")
    PlatDto toDto(Plat entity);

    // DTO (PlatDto) -> Entité (Plat)
    // On prend le Long category du DTO pour remplir l'objet category de l'entité
    @Mapping(target = "category", source = "dto.category")
    Plat toEntity(PlatDto dto);

    // Méthode de conversion indispensable pour transformer le Long en Objet Category
    default Category map(Long id) {
        if (id == null) return null;
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
