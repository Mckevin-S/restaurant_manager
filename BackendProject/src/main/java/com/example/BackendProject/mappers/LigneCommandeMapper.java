package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.dto.LigneCommandeDto;

@Mapper(componentModel = "spring")
public interface LigneCommandeMapper {

    LigneCommandeDto toDto(LigneCommande entity);

    LigneCommande toEntity(LigneCommandeDto dto);
}
