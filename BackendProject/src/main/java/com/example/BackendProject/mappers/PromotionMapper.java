package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Promotion;
import com.example.BackendProject.dto.PromotionDto;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionDto toDto(Promotion entity);

    Promotion toEntity(PromotionDto dto);
}
