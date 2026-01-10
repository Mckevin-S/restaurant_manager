package com.example.BackendProject.mappers;


import org.mapstruct.Mapper;
import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.dto.LigneCommandeDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LigneCommandeMapper {
    @Mapping(target = "commande", source = "commande.id")
    @Mapping(target = "plat", source = "plat.id")
    LigneCommandeDto toDto(LigneCommande ligneCommande);

    @Mapping(target = "commande.id", source = "commande")
    @Mapping(target = "plat.id", source = "plat")
    LigneCommande toEntity(LigneCommandeDto dto);
}
