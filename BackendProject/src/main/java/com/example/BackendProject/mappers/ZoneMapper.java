package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Zone;
import com.example.BackendProject.dto.ZoneDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TableRestaurantMapper.class})
public interface ZoneMapper {
    @Mapping(target = "restaurantId", source = "restaurant.id")
    ZoneDto toDto(Zone zone);

    @Mapping(target = "restaurant.id", source = "restaurantId")
    Zone toEntity(ZoneDto zoneDto);
}
