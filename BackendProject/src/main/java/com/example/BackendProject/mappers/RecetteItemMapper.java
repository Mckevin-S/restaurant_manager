package com.example.BackendProject.mappers;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import org.mapstruct.Mapper;
import com.example.BackendProject.entities.RecetteItem;
import com.example.BackendProject.dto.RecetteItemDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecetteItemMapper {

    // Mapping DTO -> Entité
    @Mapping(target = "recette", source = "recette")
    @Mapping(target = "ingredient", source = "ingredient")
    RecetteItem toEntity(RecetteItemDto dto);

    // Mapping Entité -> DTO
    @Mapping(target = "recette", source = "recette.id")
    @Mapping(target = "ingredient", source = "ingredient.id")
    RecetteItemDto toDto(RecetteItem entity);

    // Méthodes utilitaires pour convertir les IDs Long en Objets
    default Recette mapRecette(Long id) {
        if (id == null) return null;
        Recette r = new Recette();
        r.setId(id);
        return r;
    }

    default Ingredient mapIngredient(Long id) {
        if (id == null) return null;
        Ingredient i = new Ingredient();
        i.setId(id);
        return i;
    }
}
