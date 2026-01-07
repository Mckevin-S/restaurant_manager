package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.RecetteDto;
import com.example.BackendProject.services.implementations.RecetteServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recettes")
@Tag(
        name = "Recettes",
        description = "Gestion des recettes (composition des plats)"
)
public class RecetteController {

    private static final Logger logger = LoggerFactory.getLogger(RecetteController.class);
    private final RecetteServiceImplementation recetteServiceImplementation;

    public RecetteController(RecetteServiceImplementation recetteServiceImplementation) {
        this.recetteServiceImplementation = recetteServiceImplementation;
    }

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @Operation(
            summary = "Créer une recette",
            description = "Ajoute une nouvelle recette associée à un plat"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recette créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public RecetteDto create(
            @RequestBody RecetteDto dto,
            HttpServletRequest request
    ) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'une recette", context);
        try {
            RecetteDto savedRecette = recetteServiceImplementation.save(dto);
            logger.info("{} Recette créée avec succès. ID: {}", context, savedRecette.getId());
            return savedRecette;
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création de la recette: {}", context, e.getMessage(), e);
            throw e;
        }
    }

    // -------------------------------------------------
    // GET BY ID
    // -------------------------------------------------
    @Operation(
            summary = "Récupérer une recette par ID",
            description = "Retourne les détails d'une recette"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recette trouvée"),
            @ApiResponse(responseCode = "404", description = "Recette introuvable")
    })
    @GetMapping("/{id}")
    public RecetteDto getById(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la recette avec l'ID: {}", context, id);
        try {
            RecetteDto recette = recetteServiceImplementation.getById(id);
            logger.info("{} Recette ID: {} récupérée avec succès", context, id);
            return recette;
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération de la recette ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    // -------------------------------------------------
    // GET ALL
    // -------------------------------------------------
    @Operation(
            summary = "Lister toutes les recettes",
            description = "Retourne la liste complète des recettes"
    )
    @GetMapping
    public List<RecetteDto> getAll(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les recettes", context);
        List<RecetteDto> recettes = recetteServiceImplementation.getAll();
        logger.info("{} {} recettes récupérées avec succès", context, recettes.size());
        return recettes;
    }

    // -------------------------------------------------
    // GET BY PLAT
    // -------------------------------------------------
    @Operation(
            summary = "Lister les recettes d’un plat",
            description = "Retourne toutes les recettes associées à un plat"
    )
    @GetMapping("/plat/{platId}")
    public List<RecetteDto> getByPlat(
            @Parameter(description = "ID du plat", example = "5")
            @PathVariable Long platId,
            HttpServletRequest request
    ) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des recettes pour le plat ID: {}", context, platId);
        List<RecetteDto> recettes = recetteServiceImplementation.getByPlat(platId);
        logger.info("{} {} recettes récupérées pour le plat ID: {}", context, recettes.size(), platId);
        return recettes;
    }

    // -------------------------------------------------
    // DELETE
    // -------------------------------------------------
    @Operation(
            summary = "Supprimer une recette",
            description = "Supprime définitivement une recette"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recette supprimée"),
            @ApiResponse(responseCode = "404", description = "Recette introuvable")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID de la recette", example = "3")
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de la recette ID: {}", context, id);
        try {
            recetteServiceImplementation.delete(id);
            logger.info("{} Recette ID: {} supprimée avec succès", context, id);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression de la recette ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }
}

