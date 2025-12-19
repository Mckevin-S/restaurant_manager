package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.RecetteItemDto;

import com.example.BackendProject.services.implementations.RecetteItemServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recette-items")
@Tag(name = "Recette Items", description = "Gestion des ingrédients d'une recette")
public class RecetteItemController {

    private final RecetteItemServiceImplementation service;

    public RecetteItemController(RecetteItemServiceImplementation service) {
        this.service = service;
    }

    @Operation(summary = "Ajouter un ingrédient à une recette")
    @PostMapping
    public RecetteItemDto create(@RequestBody RecetteItemDto dto) {
        return service.save(dto);
    }

    @Operation(summary = "Récupérer un item de recette")
    @GetMapping("/{id}")
    public RecetteItemDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Lister tous les items de recette")
    @GetMapping
    public List<RecetteItemDto> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Lister les ingrédients d'une recette")
    @GetMapping("/recette/{recetteId}")
    public List<RecetteItemDto> getByRecette(@PathVariable Long recetteId) {
        return service.getByRecette(recetteId);
    }

    @Operation(summary = "Supprimer un item de recette")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

