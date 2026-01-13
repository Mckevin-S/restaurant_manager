package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.dto.CommandeDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {LigneCommandeMapper.class})
public interface CommandeMapper {
    @Mapping(target = "tableId", source = "table.id")
    @Mapping(target = "lignesCommande", source = "lignes")
    CommandeDto toDto(Commande commande);

    @Mapping(target = "table.id", source = "tableId")
    @Mapping(target = "lignes", source = "lignesCommande")
    Commande toEntity(CommandeDto commandeDto);

    @org.mapstruct.AfterMapping
    default void handleNullTable(@org.mapstruct.MappingTarget Commande entity, CommandeDto dto) {
        if (dto.getTableId() == null) {
            entity.setTable(null);
        }
    }
}
