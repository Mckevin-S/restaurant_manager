package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.RecetteItemDto;
import com.example.BackendProject.services.implementations.RecetteItemServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recette-items")
@Tag(name = "Recette Items", description = "Gestion des ingrédients d'une recette")
public class RecetteItemController {

    private static final Logger logger = LoggerFactory.getLogger(RecetteItemController.class);
    private final RecetteItemServiceImplementation service;

    public RecetteItemController(RecetteItemServiceImplementation service) {
        this.service = service;
    }

    @Operation(summary = "Ajouter un ingrédient à une recette")
    @PostMapping
    public RecetteItemDto create(@RequestBody RecetteItemDto dto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative d'ajout d'un ingrédient à une recette", context);
        try {
            RecetteItemDto saved = service.save(dto);
            logger.info("{} Ingrédient ajouté à la recette avec succès. ID: {}", context, saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("{} Erreur lors de l'ajout de l'ingrédient à la recette: {}", context, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Récupérer un item de recette")
    @GetMapping("/{id}")
    public RecetteItemDto getById(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de l'item de recette avec l'ID: {}", context, id);
        try {
            RecetteItemDto item = service.getById(id);
            logger.info("{} Item de recette ID: {} récupéré avec succès", context, id);
            return item;
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération de l'item de recette ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Lister tous les items de recette")
    @GetMapping
    public List<RecetteItemDto> getAll(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de tous les items de recette", context);
        List<RecetteItemDto> items = service.getAll();
        logger.info("{} {} items de recette récupérés avec succès", context, items.size());
        return items;
    }

    @Operation(summary = "Lister les ingrédients d'une recette")
    @GetMapping("/recette/{recetteId}")
    public List<RecetteItemDto> getByRecette(@PathVariable Long recetteId, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des ingrédients de la recette ID: {}", context, recetteId);
        List<RecetteItemDto> items = service.getByRecette(recetteId);
        logger.info("{} {} ingrédients récupérés pour la recette ID: {}", context, items.size(), recetteId);
        return items;
    }

    @Operation(summary = "Supprimer un item de recette")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de l'item de recette ID: {}", context, id);
        try {
            service.delete(id);
            logger.info("{} Item de recette ID: {} supprimé avec succès", context, id);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression de l'item de recette ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }
}

