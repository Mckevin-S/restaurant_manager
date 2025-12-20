package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.ZoneDto;
import com.example.BackendProject.services.implementations.ZoneServiceImplementation;
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
@RequestMapping("api/zones")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Zones", description = "API pour la gestion des zones du restaurant")
public class ZoneController {

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
    public ResponseEntity<?> createZone(@RequestBody ZoneDto zoneDto) {
        try {
            ZoneDto createdZone = zoneService.addZone(zoneDto);
            return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
        } catch (Exception e) {
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
            @RequestBody ZoneDto zoneDto) {
        try {
            ZoneDto updatedZone = zoneService.updateZone(id, zoneDto);
            return new ResponseEntity<>(updatedZone, HttpStatus.OK);
        } catch (Exception e) {
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
    public ResponseEntity<?> getAllZones() {
        try {
            List<ZoneDto> zones = zoneService.getAllZones();
            return new ResponseEntity<>(zones, HttpStatus.OK);
        } catch (Exception e) {
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
            @PathVariable Long id) {
        try {
            ZoneDto zone = zoneService.getZoneById(id);
            return new ResponseEntity<>(zone, HttpStatus.OK);
        } catch (Exception e) {
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
            @PathVariable Long id) {
        try {
            zoneService.deleteZone(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}