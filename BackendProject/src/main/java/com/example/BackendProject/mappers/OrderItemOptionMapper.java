package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.OrderItemOption;
import com.example.BackendProject.dto.OrderItemOptionDto;

@Mapper(componentModel = "spring")
public interface OrderItemOptionMapper {

    OrderItemOptionDto toDto(OrderItemOption entity);

    OrderItemOption toEntity(OrderItemOptionDto dto);
}
