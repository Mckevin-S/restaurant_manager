package com.example.BackendProject.services.implementations;

import com.example.BackendProject.repository.RecetteRepository;
import com.example.BackendProject.services.interfaces.RecetteServiceInterface;


import com.example.BackendProject.dto.RecetteDto;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.mappers.RecetteMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecetteServiceImplementation implements RecetteServiceInterface {

    private final RecetteRepository recetteRepository;
    private final RecetteMapper recetteMapper;

    public RecetteServiceImplementation(
            RecetteRepository recetteRepository,
            RecetteMapper recetteMapper
    ) {
        this.recetteRepository = recetteRepository;
        this.recetteMapper = recetteMapper;
    }

    @Override
    public RecetteDto save(RecetteDto dto) {
        Recette recette = recetteMapper.toEntity(dto);
        Recette saved = recetteRepository.save(recette);
        return recetteMapper.toDto(saved);
    }

    @Override
    public RecetteDto getById(Long id) {
        Recette recette = recetteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recette introuvable"));
        return recetteMapper.toDto(recette);
    }

    @Override
    public List<RecetteDto> getAll() {
        return recetteRepository.findAll()
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    @Override
    public List<RecetteDto> getByPlat(Long platId) {
        return recetteRepository.findByPlatId(platId)
                .stream()
                .map(recetteMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        recetteRepository.deleteById(id);
    }
}

