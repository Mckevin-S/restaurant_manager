package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Paiement;
import com.example.BackendProject.dto.PaiementDto;

@Mapper(componentModel = "spring")
public interface PaiementMapper {

    PaiementDto toDto(Paiement entity);

    Paiement toEntity(PaiementDto dto);
}
