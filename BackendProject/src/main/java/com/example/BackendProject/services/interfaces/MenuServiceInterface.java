package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.MenuDto;

import java.util.List;

public interface MenuServiceInterface {

    MenuDto save(MenuDto menuDto);

    List<MenuDto> getAll();

    MenuDto getById(Long id);

    MenuDto update(Long id, MenuDto menuDto);

    void delete(Long id);
}
