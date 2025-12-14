package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.CommandePromotion;
import com.example.BackendProject.dto.CommandePromotionDto;

@Mapper(componentModel = "spring")
public interface CommandePromotionMapper {

    CommandePromotionDto toDto(CommandePromotion entity);

    CommandePromotion toEntity(CommandePromotionDto dto);
}
