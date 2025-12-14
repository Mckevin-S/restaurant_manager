package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.dto.TableRestaurantDto;

@Mapper(componentModel = "spring")
public interface TableRestaurantMapper {

    TableRestaurantDto toDto(TableRestaurant entity);

    TableRestaurant toEntity(TableRestaurantDto dto);
}
