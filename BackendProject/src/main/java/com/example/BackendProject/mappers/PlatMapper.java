package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.dto.PlatDto;

@Mapper(componentModel = "spring")
public interface PlatMapper {

    PlatDto toDto(Plat entity);

    Plat toEntity(PlatDto dto);
}
