package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.CommandePromotion;
import com.example.BackendProject.dto.CommandePromotionDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommandePromotionMapper {

    @Mapping(source = "commande.id", target = "commande")
    @Mapping(source = "promotion.id", target = "promotion")
    CommandePromotionDto toDto(CommandePromotion entity);

    @Mapping(source = "commande", target = "commande.id")
    @Mapping(source = "promotion", target = "promotion.id")
    CommandePromotion toEntity(CommandePromotionDto dto);
}