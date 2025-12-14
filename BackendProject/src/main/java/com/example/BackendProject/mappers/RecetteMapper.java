package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.dto.RecetteDto;

@Mapper(componentModel = "spring")
public interface RecetteMapper {

    RecetteDto toDto(Recette entity);

    Recette toEntity(RecetteDto dto);
}
