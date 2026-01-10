package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.dto.PlatDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatMapper {
    // Vers DTO : On prend l'ID de la catégorie et on utilise le DTO pour la recette
    @Mapping(target = "category", source = "category.id")
    PlatDto toDto(Plat plat);

    // Vers Entité : On reconstruit l'objet Category avec l'ID pour Hibernate
    @Mapping(target = "category.id", source = "category")
    Plat toEntity(PlatDto platDto);

}
