package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.dto.CommandeDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommandeMapper {
    @Mapping(target = "tableId", source = "table.id")
    @Mapping(target = "serveurId", source = "serveur.id")
    CommandeDto toDto(Commande commande);

    @Mapping(target = "table.id", source = "tableId")
    @Mapping(target = "serveur.id", source = "serveurId")
    Commande toEntity(CommandeDto commandeDto);
}
