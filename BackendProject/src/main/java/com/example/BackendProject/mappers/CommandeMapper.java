package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.dto.CommandeDto;

@Mapper(componentModel = "spring")
public interface CommandeMapper {

    CommandeDto toDto(Commande entity);

    Commande toEntity(CommandeDto dto);
}
