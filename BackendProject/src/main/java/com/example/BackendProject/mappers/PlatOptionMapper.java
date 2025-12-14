package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.PlatOption;
import com.example.BackendProject.dto.PlatOptionDto;

@Mapper(componentModel = "spring")
public interface PlatOptionMapper {

    PlatOptionDto toDto(PlatOption entity);

    PlatOption toEntity(PlatOptionDto dto);
}
