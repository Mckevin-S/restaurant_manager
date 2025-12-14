package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Zone;
import com.example.BackendProject.dto.ZoneDto;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneDto toDto(Zone entity);

    Zone toEntity(ZoneDto dto);
}
