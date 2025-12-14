package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.RecetteItem;
import com.example.BackendProject.dto.RecetteItemDto;

@Mapper(componentModel = "spring")
public interface RecetteItemMapper {

    RecetteItemDto toDto(RecetteItem entity);

    RecetteItem toEntity(RecetteItemDto dto);
}
