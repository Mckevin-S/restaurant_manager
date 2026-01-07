package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.ZoneDto;
import com.example.BackendProject.services.implementations.ZoneServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
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
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("api/zones")
@Tag(name = "Gestion des Zones", description = "API pour la gestion des zones du restaurant")
public class ZoneController {

    private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);
    private final ZoneServiceImplementation zoneService;

    public ZoneController(ZoneServiceImplementation zoneService) {
        this.zoneService = zoneService;
    }

    /**
     * Créer une nouvelle zone
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle zone", description = "Ajoute une nouvelle zone dans le restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Zone créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ZoneDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur de validation ou données invalides")
    })
    public ResponseEntity<?> createZone(@RequestBody ZoneDto zoneDto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'une zone - Nom: {}", context, zoneDto.getNom());
        try {
            ZoneDto createdZone = zoneService.addZone(zoneDto);
            logger.info("{} Zone créée avec succès. ID: {}, Nom: {}", context, createdZone.getId(), createdZone.getNom());
            return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création de la zone: {}", context, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la création : " + e.getMessage());
        }
    }

    /**
     * Mettre à jour une zone
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une zone", description = "Met à jour les informations d'une zone existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zone mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    })
    public ResponseEntity<?> updateZone(
            @Parameter(description = "ID de la zone", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody ZoneDto zoneDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour de la zone ID: {}", context, id);
        try {
            ZoneDto updatedZone = zoneService.updateZone(id, zoneDto);
            logger.info("{} Zone ID: {} mise à jour avec succès", context, id);
            return new ResponseEntity<>(updatedZone, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour de la zone ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zone non trouvée : " + e.getMessage());
        }
    }

    /**
     * Récupérer toutes les zones
     */
    @GetMapping
    @Operation(summary = "Récupérer toutes les zones", description = "Liste toutes les zones disponibles")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ZoneDto.class))))
    public ResponseEntity<?> getAllZones(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les zones", context);
        try {
            List<ZoneDto> zones = zoneService.getAllZones();
            logger.info("{} {} zones récupérées avec succès", context, zones.size());
            return new ResponseEntity<>(zones, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération des zones: {}", context, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de récupération : " + e.getMessage());
        }
    }

    /**
     * Récupérer une zone par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une zone par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zone trouvée"),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    })
    public ResponseEntity<?> getZoneById(
            @Parameter(description = "ID de la zone", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la zone avec l'ID: {}", context, id);
        try {
            ZoneDto zone = zoneService.getZoneById(id);
            logger.info("{} Zone ID: {} récupérée avec succès", context, id);
            return new ResponseEntity<>(zone, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération de la zone ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zone non trouvée");
        }
    }

    /**
     * Supprimer une zone
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une zone", description = "Supprime une zone existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Zone supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    })
    public ResponseEntity<?> deleteZone(
            @Parameter(description = "ID de la zone", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de la zone ID: {}", context, id);
        try {
            zoneService.deleteZone(id);
            logger.info("{} Zone ID: {} supprimée avec succès", context, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression de la zone ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}