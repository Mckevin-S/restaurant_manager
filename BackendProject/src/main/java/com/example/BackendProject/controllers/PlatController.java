package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.services.implementations.PlatServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plats")
@Tag(name = "Gestion des Plats", description = "API pour la gestion des plats (entrées, plats principaux, desserts) du restaurant")
public class PlatController {

    private static final Logger logger = LoggerFactory.getLogger(PlatController.class);
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
            @Valid @RequestBody PlatDto platDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'un plat", context);
        try {
            PlatDto savedPlat = platServiceImplementation.save(platDto);
            logger.info("{} Plat créé avec succès. ID: {}", context, savedPlat.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlat);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création du plat: {}", context, e.getMessage(), e);
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
    public ResponseEntity<List<PlatDto>> getAllPlats(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de tous les plats", context);
        List<PlatDto> plats = platServiceImplementation.getAll();
        logger.info("{} {} plats récupérés avec succès", context, plats.size());
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
            @Valid @RequestBody PlatDto platDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour du plat ID: {}", context, id);
        try {
            PlatDto updatedPlat = platServiceImplementation.update(id, platDto);
            logger.info("{} Plat ID: {} mis à jour avec succès", context, id);
            return ResponseEntity.ok(updatedPlat);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la mise à jour du plat ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
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
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération du plat avec l'ID: {}", context, id);
        try {
            PlatDto plat = platServiceImplementation.getById(id);
            logger.info("{} Plat ID: {} récupéré avec succès", context, id);
            return ResponseEntity.ok(plat);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération du plat ID: {} - {}", context, id, e.getMessage(), e);
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
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression du plat ID: {}", context, id);
        try {
            platServiceImplementation.delete(id);
            logger.info("{} Plat ID: {} supprimé avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la suppression du plat ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Retrouver le plat le plus vendu
     */

    @GetMapping("/plus-vendus")
//    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Obtenir le classement des plats les plus populaires")
    public ResponseEntity<List<Map<String, Object>>> getTopPlats(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des plats les plus vendus", context);
        List<Map<String, Object>> topPlats = platServiceImplementation.getStatistiquesPlatsVendus();
        logger.info("{} Statistiques des plats les plus vendus récupérées avec succès", context);
        return ResponseEntity.ok(topPlats);
    }

    /**
     * Recuperer les plats disponibles uniquement (La carte)
     */

    @GetMapping("/disponibles")
    @Operation(summary = "Récupérer la carte du restaurant (Plats disponibles uniquement)")
    public ResponseEntity<List<PlatDto>> getMenu(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la carte du restaurant (plats disponibles)", context);
        List<PlatDto> platsDisponibles = platServiceImplementation.getMenuActif();
        logger.info("{} {} plats disponibles récupérés avec succès", context, platsDisponibles.size());
        return ResponseEntity.ok(platsDisponibles);
    }

    /**
     * Changer la disponibilité d'un plat
     */

    @PatchMapping("/{id}/statut-disponibilite")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUISINIER')")
    @Operation(summary = "Marquer un plat comme disponible ou indisponible")
    public ResponseEntity<PlatDto> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean disponible,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Modification du statut de disponibilité du plat ID: {} - Disponible: {}", context, id, disponible);
        try {
            PlatDto updatedPlat = platServiceImplementation.modifierDisponibilite(id, disponible);
            logger.info("{} Statut de disponibilité du plat ID: {} mis à jour avec succès", context, id);
            return ResponseEntity.ok(updatedPlat);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la modification du statut de disponibilité du plat ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Upload l'image d'un plat
     */

    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER', 'CUISINIER')")
    @Operation(summary = "Uploader et redimensionner la photo d'un plat")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative d'upload d'image pour le plat ID: {} - Nom du fichier: {}", context, id, file.getOriginalFilename());
        try {
            PlatDto updatedPlat = platServiceImplementation.uploadPlatImage(id, file);
            logger.info("{} Image uploadée avec succès pour le plat ID: {}", context, id);
            return ResponseEntity.ok(updatedPlat);
        } catch (IOException e) {
            logger.error("{} Erreur IO lors de l'upload d'image pour le plat ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement de l'image : " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de l'upload d'image pour le plat ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}