package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.dto.RestaurantDto;

@Mapper(componentModel = "spring", uses = JsonMapper.class)
public interface RestaurantMapper {

    @Mapping(target = "heuresOuverture", source = "heuresOuverture")
    RestaurantDto toDto(Restaurant entity);

    @Mapping(target = "heuresOuverture", source = "heuresOuverture")
    Restaurant toEntity(RestaurantDto dto);
}
