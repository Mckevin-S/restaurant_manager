package com.example.BackendProject.mappers;

import com.example.BackendProject.dto.CommandePromotionDto;
import com.example.BackendProject.entities.CommandePromotion;
import org.mapstruct.Mapper;
import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.dto.LigneCommandeDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LigneCommandeMapper {
    @Mapping(source = "commande.id", target = "commande")
    @Mapping(source = "plat.id", target = "plat")
    LigneCommandeDto toDto(LigneCommande entity);

    @Mapping(source = "commande", target = "commande.id")
    @Mapping(source = "plat", target = "plat.id")
    LigneCommande toEntity(LigneCommandeDto dto);
}
