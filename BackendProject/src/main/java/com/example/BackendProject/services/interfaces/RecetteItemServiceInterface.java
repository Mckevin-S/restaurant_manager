package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.RecetteItemDto;

import java.util.List;

public interface RecetteItemServiceInterface {

    RecetteItemDto save(RecetteItemDto dto);

    RecetteItemDto getById(Long id);

    List<RecetteItemDto> getAll();

    List<RecetteItemDto> getByRecette(Long recetteId);

    void delete(Long id);

}
