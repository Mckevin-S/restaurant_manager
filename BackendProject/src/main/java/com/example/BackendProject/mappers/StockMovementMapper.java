package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.StockMovement;
import com.example.BackendProject.dto.StockMovementDto;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    StockMovementDto toDto(StockMovement entity);

    StockMovement toEntity(StockMovementDto dto);
}
