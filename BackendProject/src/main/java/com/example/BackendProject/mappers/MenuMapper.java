package com.example.BackendProject.mappers;

import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.dto.MenuDto;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    MenuDto toDto(Menu entity);

    Menu toEntity(MenuDto dto);
}
