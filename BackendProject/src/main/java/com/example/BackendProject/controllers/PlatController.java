package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.services.implementations.PlatServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plats")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Plats", description = "API pour la gestion des plats (entrées, plats principaux, desserts) du restaurant")
public class PlatController {

    private final PlatServiceImplementation platServiceImplementation;

    public PlatController(PlatServiceImplementation platServiceImplementation) {
        this.platServiceImplementation = platServiceImplementation;
    }

    /**
     * Créer un nouveau plat
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouveau plat",
            description = "Permet d'ajouter un nouveau plat en le liant à une catégorie existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Plat créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlatDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides ou catégorie non trouvée"
            )
    })
    public ResponseEntity<PlatDto> createPlat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du plat à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlatDto.class))
            )
            @RequestBody PlatDto platDto) {
        try {
            PlatDto savedPlat = platServiceImplementation.save(platDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Récupérer tous les plats
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les plats",
            description = "Retourne la liste complète de tous les plats de la carte"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des plats récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PlatDto.class))
            )
    )
    public ResponseEntity<List<PlatDto>> getAllPlats() {
        List<PlatDto> plats = platServiceImplementation.getAll();
        return ResponseEntity.ok(plats);
    }


    /**
     * Modifier un plat
     */


    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un plat existant",
            description = "Modifie les détails d'un plat en fonction de son ID. Si l'ID n'existe pas, une erreur est retournée."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plat mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Plat non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    public ResponseEntity<PlatDto> update(
            @Parameter(description = "ID du plat à modifier", example = "1")
            @PathVariable Long id,
            @RequestBody PlatDto platDto) {

        PlatDto updatedPlat = platServiceImplementation.update(id, platDto);
        return ResponseEntity.ok(updatedPlat);
    }




    /**
     * Récupérer un plat par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un plat par ID",
            description = "Retourne les détails d'un plat spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plat trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlatDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plat non trouvé"
            )
    })
    public ResponseEntity<PlatDto> getPlatById(
            @Parameter(description = "ID du plat", required = true, example = "1")
            @PathVariable Long id) {
        try {
            PlatDto plat = platServiceImplementation.getById(id);
            return ResponseEntity.ok(plat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Supprimer un plat
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un plat",
            description = "Supprime définitivement un plat du système"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Plat supprimé avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plat non trouvé"
            )
    })
    public ResponseEntity<Void> deletePlat(
            @Parameter(description = "ID du plat à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            platServiceImplementation.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}