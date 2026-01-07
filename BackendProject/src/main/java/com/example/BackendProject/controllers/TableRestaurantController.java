package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.TableRestaurantDto;
import com.example.BackendProject.services.implementations.TableRestaurantServiceImplementation;
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
@RequestMapping("api/tables")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Tables", description = "API pour la gestion des tables du restaurant")
public class TableRestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(TableRestaurantController.class);
    private final TableRestaurantServiceImplementation tableService;

    public TableRestaurantController(TableRestaurantServiceImplementation tableService) {
        this.tableService = tableService;
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle table", description = "Ajoute une nouvelle table dans une zone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Table créée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TableRestaurantDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur de validation")
    })
    public ResponseEntity<TableRestaurantDto> createTable(@RequestBody TableRestaurantDto tableDto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'une table - Numéro: {}", context, tableDto.getNumero());
        try {
            TableRestaurantDto created = tableService.addTable(tableDto);
            logger.info("{} Table créée avec succès. ID: {}, Numéro: {}", context, created.getId(), created.getNumero());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création de la table: {}", context, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une table", description = "Met à jour les informations d'une table existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table mise à jour avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TableRestaurantDto.class))),
            @ApiResponse(responseCode = "404", description = "Table non trouvée")
    })
    public ResponseEntity<?> updateTable(
            @Parameter(description = "ID de la table", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody TableRestaurantDto tableDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour de la table ID: {}", context, id);
        try {
            TableRestaurantDto updated = tableService.updateTable(id, tableDto);
            logger.info("{} Table ID: {} mise à jour avec succès", context, id);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour de la table ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Table non trouvée: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les tables", description = "Liste toutes les tables disponibles")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TableRestaurantDto.class))))
    public ResponseEntity<List<TableRestaurantDto>> getAllTables(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les tables", context);
        try {
            List<TableRestaurantDto> tables = tableService.getAllTables();
            logger.info("{} {} tables récupérées avec succès", context, tables.size());
            return new ResponseEntity<>(tables, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération des tables: {}", context, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une table par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table trouvée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TableRestaurantDto.class))),
            @ApiResponse(responseCode = "404", description = "Table non trouvée")
    })
    public ResponseEntity<TableRestaurantDto> getTableById(
            @Parameter(description = "ID de la table", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la table avec l'ID: {}", context, id);
        try {
            TableRestaurantDto dto = tableService.getTableById(id);
            logger.info("{} Table ID: {} récupérée avec succès", context, id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération de la table ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une table", description = "Supprime une table existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Table supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Table non trouvée")
    })
    public ResponseEntity<Void> deleteTable(
            @Parameter(description = "ID de la table", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de la table ID: {}", context, id);
        try {
            tableService.deleteTable(id);
            logger.info("{} Table ID: {} supprimée avec succès", context, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression de la table ID: {} - {}", context, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/zone/{zoneId}")
    @Operation(summary = "Récupérer les tables par zone", description = "Retourne les tables appartenant à une zone donnée")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TableRestaurantDto.class))))
    public ResponseEntity<List<TableRestaurantDto>> getTablesByZone(
            @Parameter(description = "ID de la zone", required = true, example = "1")
            @PathVariable Long zoneId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des tables pour la zone ID: {}", context, zoneId);
        try {
            List<TableRestaurantDto> tables = tableService.getTablesByZoneId(zoneId);
            logger.info("{} {} tables récupérées pour la zone ID: {}", context, tables.size(), zoneId);
            return new ResponseEntity<>(tables, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération des tables pour la zone ID: {} - {}", context, zoneId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des tables par numéro", description = "Recherche partielle sur le numéro de table")
    @ApiResponse(responseCode = "200", description = "Résultat de la recherche",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TableRestaurantDto.class))))
    public ResponseEntity<List<TableRestaurantDto>> searchTablesByNumero(
            @Parameter(description = "Terme de recherche pour le numéro", required = true, example = "A1")
            @RequestParam String numero,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Recherche de tables par numéro: {}", context, numero);
        try {
            List<TableRestaurantDto> tables = tableService.searchTablesByNumero(numero);
            logger.info("{} {} tables trouvées pour le numéro: {}", context, tables.size(), numero);
            return new ResponseEntity<>(tables, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la recherche de tables par numéro {} - {}", context, numero, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
