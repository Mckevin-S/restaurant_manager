package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.CommandePromotionPK;
import com.example.BackendProject.dto.CommandePromotionPKDto;

@Mapper(componentModel = "spring")
public interface CommandePromotionPKMapper {

    CommandePromotionPKDto toDto(CommandePromotionPK entity);

    CommandePromotionPK toEntity(CommandePromotionPKDto dto);
}
