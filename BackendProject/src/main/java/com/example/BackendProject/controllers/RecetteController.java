package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.RecetteDto;
import com.example.BackendProject.services.implementations.RecetteServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recettes")
@Tag(
        name = "Recettes",
        description = "Gestion des recettes (composition des plats)"
)
public class RecetteController {

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
            @RequestBody RecetteDto dto
    ) {
        return recetteServiceImplementation.save(dto);
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
            @PathVariable Long id
    ) {
        return recetteServiceImplementation.getById(id);
    }

    // -------------------------------------------------
    // GET ALL
    // -------------------------------------------------
    @Operation(
            summary = "Lister toutes les recettes",
            description = "Retourne la liste complète des recettes"
    )
    @GetMapping
    public List<RecetteDto> getAll() {
        return recetteServiceImplementation.getAll();
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
            @PathVariable Long platId
    ) {
        return recetteServiceImplementation.getByPlat(platId);
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
            @PathVariable Long id
    ) {
        recetteServiceImplementation.delete(id);
    }
}

