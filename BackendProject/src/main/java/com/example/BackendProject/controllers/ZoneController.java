package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.ZoneDto;
import com.example.BackendProject.services.implementations.ZoneServiceImplementation;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
=======
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
import java.util.List;

@RestController
@RequestMapping("/zones")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Zones", description = "API pour la gestion des zones du restaurant")
public class ZoneController {

    private final ZoneServiceImplementation zoneService;
=======
@RestController
@RequestMapping({"/zones"})
public class ZoneController {
    private ZoneServiceImplementation zoneService;
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8

    public ZoneController(ZoneServiceImplementation zoneService) {
        this.zoneService = zoneService;
    }
<<<<<<< HEAD

    /**
     * Créer une nouvelle zone
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle zone", description = "Ajoute une nouvelle zone dans le restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Zone créée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ZoneDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur de validation ou zone déjà existante")
    })
    public ResponseEntity<ZoneDto> createZone(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Détails de la zone à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ZoneDto.class))
            )
            @RequestBody ZoneDto zoneDto) {
=======
    @PostMapping
    public ResponseEntity<?> createZone(ZoneDto zoneDto) {
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
        try {
            ZoneDto createdZone = zoneService.addZone(zoneDto);
            return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
        } catch (Exception e) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Mettre à jour une zone
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une zone", description = "Met à jour les informations d'une zone existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zone mise à jour avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ZoneDto.class))),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    })
    public ResponseEntity<?> updateZone(
            @Parameter(description = "ID de la zone", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody ZoneDto zoneDto) {
=======
            return ResponseEntity.status(500).body("Error creating zone: " + e.getMessage());
        }

    }
    @PutMapping
    public ResponseEntity<?> updateZone(Long id, ZoneDto zoneDto) {
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
        try {
            ZoneDto updatedZone = zoneService.updateZone(id, zoneDto);
            return new ResponseEntity<>(updatedZone, HttpStatus.OK);
        } catch (Exception e) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zone non trouvée: " + e.getMessage());
        }
    }

    /**
     * Récupérer toutes les zones
     */
    @GetMapping
    @Operation(summary = "Récupérer toutes les zones", description = "Liste toutes les zones disponibles")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ZoneDto.class))))
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        try {
            return new ResponseEntity<>(zoneService.getAllZones(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Récupérer une zone par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une zone par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zone trouvée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ZoneDto.class))),
            @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    })
    public ResponseEntity<ZoneDto> getZoneById(
            @Parameter(description = "ID de la zone", required = true, example = "1")
            @PathVariable Long id) {
=======
            return ResponseEntity.status(500).body("Error updating zone: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllZones() {
        try {
            return new ResponseEntity<>(zoneService.getAllZones(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving zones: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getZoneById(@PathVariable Long id) {
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
        try {
            ZoneDto zone = zoneService.getZoneById(id);
            return new ResponseEntity<>(zone, HttpStatus.OK);
        } catch (Exception e) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
    public ResponseEntity<Void> deleteZone(
            @Parameter(description = "ID de la zone", required = true, example = "1")
            @PathVariable Long id) {
=======
            return ResponseEntity.status(500).body("Error retrieving zone: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteZone(@PathVariable Long id) {
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
        try {
            zoneService.deleteZone(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }


=======
            return ResponseEntity.status(500).body("Error deleting zone: " + e.getMessage());
        }
    }



>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
}
