package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.dto.RestaurantDto;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    RestaurantDto toDto(Restaurant entity);

    Restaurant toEntity(RestaurantDto dto);
}
