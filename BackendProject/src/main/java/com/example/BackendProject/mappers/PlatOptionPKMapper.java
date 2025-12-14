package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.PlatOptionPK;
import com.example.BackendProject.dto.PlatOptionPKDto;

@Mapper(componentModel = "spring")
public interface PlatOptionPKMapper {

    PlatOptionPKDto toDto(PlatOptionPK entity);

    PlatOptionPK toEntity(PlatOptionPKDto dto);
}
