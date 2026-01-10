package com.example.BackendProject.mappers;

import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Plat;
import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.dto.RecetteDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecetteMapper {

    RecetteDto toDto(Recette entity);

    Recette toEntity(RecetteDto dto);


    // Méthode de conversion indispensable pour transformer le Long en Objet Category
    default Plat map(Long id) {
        if (id == null) return null;
        Plat plat = new Plat();
        plat.setId(id);
        return plat;
    }
}
