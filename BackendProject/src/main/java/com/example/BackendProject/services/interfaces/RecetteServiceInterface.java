package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.RecetteDto;

import java.util.List;

public interface RecetteServiceInterface {

    RecetteDto save(RecetteDto dto);

    RecetteDto getById(Long id);

    List<RecetteDto> getAll();

    List<RecetteDto> getByPlat(Long platId);

    void delete(Long id);

}
