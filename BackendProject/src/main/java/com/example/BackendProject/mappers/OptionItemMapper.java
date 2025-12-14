package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.OptionItem;
import com.example.BackendProject.dto.OptionItemDto;

@Mapper(componentModel = "spring")
public interface OptionItemMapper {

    OptionItemDto toDto(OptionItem entity);

    OptionItem toEntity(OptionItemDto dto);
}
