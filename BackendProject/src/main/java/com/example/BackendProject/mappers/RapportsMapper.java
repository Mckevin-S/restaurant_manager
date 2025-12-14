package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Rapports;
import com.example.BackendProject.dto.RapportsDto;

@Mapper(componentModel = "spring")
public interface RapportsMapper {

    RapportsDto toDto(Rapports entity);

    Rapports toEntity(RapportsDto dto);
}
