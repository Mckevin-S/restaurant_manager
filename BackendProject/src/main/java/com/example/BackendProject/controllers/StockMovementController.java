package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.StockMovementDto;
import com.example.BackendProject.services.interfaces.StockMovementServiceInterface;
import com.example.BackendProject.utils.TypeMouvement;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/stock-movements")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Mouvements de Stock", description = "API pour la gestion des mouvements de stock des ingrédients")
public class StockMovementController {

    private final StockMovementServiceInterface stockMovementService;

    public StockMovementController(StockMovementServiceInterface stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    /**
     * Créer un nouveau mouvement de stock
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau mouvement de stock", description = "Enregistre une entrée ou sortie de stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mouvement créé avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockMovementDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur de validation"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> createStockMovement(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Détails du mouvement de stock à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StockMovementDto.class))
            )
            @RequestBody StockMovementDto stockMovementDto) {
        try {
            StockMovementDto created = stockMovementService.createStockMovement(stockMovementDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur de validation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du mouvement: " + e.getMessage());
        }
    }

    /**
     * Mettre à jour un mouvement de stock
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un mouvement de stock", description = "Met à jour les informations d'un mouvement existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mouvement mis à jour",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockMovementDto.class))),
            @ApiResponse(responseCode = "404", description = "Mouvement non trouvé"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation")
    })
    public ResponseEntity<?> updateStockMovement(
            @Parameter(description = "ID du mouvement", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du mouvement",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StockMovementDto.class))
            )
            @RequestBody StockMovementDto stockMovementDto) {
        try {
            StockMovementDto updated = stockMovementService.updateStockMovement(id, stockMovementDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur de validation: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mouvement non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    /**
     * Récupérer tous les mouvements de stock
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les mouvements de stock", description = "Liste tous les mouvements de stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StockMovementDto.class)))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getAllStockMovements() {
        try {
            List<StockMovementDto> movements = stockMovementService.getAllStockMovements();
            return new ResponseEntity<>(movements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des mouvements: " + e.getMessage());
        }
    }

    /**
     * Récupérer un mouvement de stock par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un mouvement par ID", description = "Récupère les détails d'un mouvement spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mouvement trouvé",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockMovementDto.class))),
            @ApiResponse(responseCode = "404", description = "Mouvement non trouvé")
    })
    public ResponseEntity<?> getStockMovementById(
            @Parameter(description = "ID du mouvement", required = true, example = "1")
            @PathVariable Long id) {
        try {
            StockMovementDto dto = stockMovementService.getStockMovementById(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mouvement non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    /**
     * Supprimer un mouvement de stock
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un mouvement de stock", description = "Supprime un mouvement existant et ajuste le stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mouvement supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Mouvement non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> deleteStockMovement(
            @Parameter(description = "ID du mouvement à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            stockMovementService.deleteStockMovement(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mouvement non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    /**
     * Récupérer les mouvements par ingrédient
     */
    @GetMapping("/ingredient/{ingredientId}")
    @Operation(summary = "Récupérer les mouvements par ingrédient", description = "Liste tous les mouvements d'un ingrédient spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StockMovementDto.class)))),
            @ApiResponse(responseCode = "404", description = "Ingrédient non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getStockMovementsByIngredient(
            @Parameter(description = "ID de l'ingrédient", required = true, example = "1")
            @PathVariable Long ingredientId) {
        try {
            List<StockMovementDto> movements = stockMovementService.getStockMovementsByIngredientId(ingredientId);
            return new ResponseEntity<>(movements, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ingrédient non trouvé avec l'ID: " + ingredientId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    /**
     * Récupérer les mouvements par type
     */
    @GetMapping("/type/{typeMouvement}")
    @Operation(summary = "Récupérer les mouvements par type", description = "Liste tous les mouvements d'un type spécifique (ENTREE ou SORTIE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StockMovementDto.class)))),
            @ApiResponse(responseCode = "400", description = "Type de mouvement invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getStockMovementsByType(
            @Parameter(description = "Type de mouvement (ENTREE ou SORTIE)", required = true, example = "ENTREE")
            @PathVariable TypeMouvement typeMouvement) {
        try {
            List<StockMovementDto> movements = stockMovementService.getStockMovementsByType(typeMouvement);
            return new ResponseEntity<>(movements, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Type de mouvement invalide: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    /**
     * Récupérer les mouvements entre deux dates
     */
    @GetMapping("/between")
    @Operation(summary = "Récupérer les mouvements entre deux dates", description = "Liste les mouvements dans une période donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StockMovementDto.class)))),
            @ApiResponse(responseCode = "400", description = "Dates invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getStockMovementsBetweenDates(
            @Parameter(description = "Date de début (format: yyyy-MM-dd HH:mm:ss)", required = true, example = "2024-01-01 00:00:00")
            @RequestParam String startDate,
            @Parameter(description = "Date de fin (format: yyyy-MM-dd HH:mm:ss)", required = true, example = "2024-12-31 23:59:59")
            @RequestParam String endDate) {
        try {
            Timestamp start = Timestamp.valueOf(startDate);
            Timestamp end = Timestamp.valueOf(endDate);
            List<StockMovementDto> movements = stockMovementService.getStockMovementsBetweenDates(start, end);
            return new ResponseEntity<>(movements, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Format de date invalide. Utilisez: yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    /**
     * Calculer la quantité totale en stock pour un ingrédient
     */
    @GetMapping("/ingredient/{ingredientId}/total")
    @Operation(summary = "Calculer le stock total d'un ingrédient", description = "Calcule la quantité totale disponible en stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantité calculée avec succès"),
            @ApiResponse(responseCode = "404", description = "Ingrédient non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getTotalQuantityByIngredient(
            @Parameter(description = "ID de l'ingrédient", required = true, example = "1")
            @PathVariable Long ingredientId) {
        try {
            BigDecimal total = stockMovementService.getTotalQuantityByIngredient(ingredientId);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ingrédient non trouvé avec l'ID: " + ingredientId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du calcul: " + e.getMessage());
        }
    }
}