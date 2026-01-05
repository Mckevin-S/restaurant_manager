package com.example.BackendProject.services.implementations;

import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.repository.RecetteItemRepository;
import com.example.BackendProject.repository.RecetteRepository;
import com.example.BackendProject.services.interfaces.RecetteItemServiceInterface;
import com.example.BackendProject.dto.RecetteItemDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.entities.RecetteItem;
import com.example.BackendProject.mappers.RecetteItemMapper;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecetteItemServiceImplementation implements RecetteItemServiceInterface {

    private final RecetteItemRepository recetteItemRepository;
    private final RecetteRepository recetteRepository;
    private final IngredientRepository ingredientRepository;
    private final RecetteItemMapper mapper;

    public RecetteItemServiceImplementation(
            RecetteItemRepository recetteItemRepository,
            RecetteRepository recetteRepository,
            IngredientRepository ingredientRepository,
            RecetteItemMapper mapper
    ) {
        this.recetteItemRepository = recetteItemRepository;
        this.recetteRepository = recetteRepository;
        this.ingredientRepository = ingredientRepository;
        this.mapper = mapper;
    }

    @Override
    public RecetteItemDto save(RecetteItemDto dto) {

        Recette recette = recetteRepository.findById(dto.getRecette())
                .orElseThrow(() -> new RuntimeException("Recette introuvable"));

        Ingredient ingredient = ingredientRepository.findById(dto.getIngredient())
                .orElseThrow(() -> new RuntimeException("Ingrédient introuvable"));

        RecetteItem entity = mapper.toEntity(dto);
        entity.setRecette(recette);
        entity.setIngredient(ingredient);

        return mapper.toDto(recetteItemRepository.save(entity));
    }

    @Override
    public RecetteItemDto getById(Long id) {
        return recetteItemRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("RecetteItem introuvable"));
    }

    @Override
    public List<RecetteItemDto> getAll() {
        return recetteItemRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<RecetteItemDto> getByRecette(Long recetteId) {
        return recetteItemRepository.findByRecetteId(recetteId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        recetteItemRepository.deleteById(id);
    }
}

