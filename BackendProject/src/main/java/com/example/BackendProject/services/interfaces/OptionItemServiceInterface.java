package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.OptionItemDto;

import java.util.List;

public interface OptionItemServiceInterface {
    OptionItemDto save(OptionItemDto dto);
    OptionItemDto update(Long id, OptionItemDto dto);
    List<OptionItemDto> getAll();
    OptionItemDto getById(Long id);
    void delete(Long id);
}
