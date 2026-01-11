package com.example.BackendProject.mappers;

import com.example.BackendProject.dto.ReservationDto;
import com.example.BackendProject.entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "tableId", source = "table.id")
    @Mapping(target = "numeroTable", source = "table.numero")
    ReservationDto toDto(Reservation reservation);

    @Mapping(target = "table.id", source = "tableId")
    Reservation toEntity(ReservationDto dto);
}
