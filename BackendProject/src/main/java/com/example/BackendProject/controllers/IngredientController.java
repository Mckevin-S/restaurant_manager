package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.IngredientDto;
import com.example.BackendProject.services.implementations.IngredientServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Ingrédients", description = "API pour la gestion du stock d'ingrédients")
public class IngredientController {

    private static final Logger logger = LoggerFactory.getLogger(IngredientController.class);
    private final IngredientServiceImplementation ingredientService;

    public IngredientController(IngredientServiceImplementation ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * Créer un nouvel ingrédient
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouvel ingrédient",
            description = "Permet d'ajouter un nouvel ingrédient au stock"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ingrédient créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IngredientDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides ou ingrédient déjà existant",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Un ingrédient avec ce nom existe déjà\"}")
                    )
            )
    })
    public ResponseEntity<?> createIngredient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de l'ingrédient à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = IngredientDto.class))
            )
            @RequestBody IngredientDto ingredientDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'un ingrédient: {}", context, 
                    ingredientDto.getNom() != null ? ingredientDto.getNom() : "N/A");
        try {
            IngredientDto savedIngredient = ingredientService.save(ingredientDto);
            logger.info("{} Ingrédient créé avec succès. ID: {} - Nom: {}", 
                       context, savedIngredient.getId(), savedIngredient.getNom());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedIngredient);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la création de l'ingrédient: {}", context, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Récupérer tous les ingrédients
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les ingrédients",
            description = "Retourne la liste complète de tous les ingrédients en stock"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des ingrédients récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))
            )
    )
    public ResponseEntity<List<IngredientDto>> getAllIngredients(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de tous les ingrédients", context);
        List<IngredientDto> ingredients = ingredientService.getAll();
        logger.info("{} {} ingrédients récupérés avec succès", context, ingredients.size());
        return ResponseEntity.ok(ingredients);
    }

    /**
     * Récupérer un ingrédient par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un ingrédient par ID",
            description = "Retourne les détails d'un ingrédient spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ingrédient trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IngredientDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ingrédient non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Ingrédient non trouvé avec l'ID : 1\"}")
                    )
            )
    })
    public ResponseEntity<?> getIngredientById(
            @Parameter(description = "ID de l'ingrédient", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de l'ingrédient avec l'ID: {}", context, id);
        try {
            IngredientDto ingredient = ingredientService.getById(id);
            logger.info("{} Ingrédient ID: {} récupéré avec succès - Nom: {}", 
                       context, id, ingredient.getNom());
            return ResponseEntity.ok(ingredient);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération de l'ingrédient ID: {} - {}", 
                        context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Mettre à jour un ingrédient
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un ingrédient",
            description = "Met à jour les informations d'un ingrédient existant. Seuls les champs fournis sont modifiés."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ingrédient mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IngredientDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ingrédient non trouvé"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            )
    })
    public ResponseEntity<?> updateIngredient(
            @Parameter(description = "ID de l'ingrédient à modifier", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de l'ingrédient",
                    required = true
            )
            @RequestBody IngredientDto ingredientDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour de l'ingrédient ID: {}", context, id);
        try {
            IngredientDto updatedIngredient = ingredientService.update(id, ingredientDto);
            logger.info("{} Ingrédient ID: {} mis à jour avec succès", context, id);
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la mise à jour de l'ingrédient ID: {} - {}", 
                        context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Supprimer un ingrédient
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un ingrédient",
            description = "Supprime définitivement un ingrédient du stock"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Ingrédient supprimé avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ingrédient non trouvé"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Impossible de supprimer un ingrédient utilisé dans des recettes"
            )
    })
    public ResponseEntity<?> deleteIngredient(
            @Parameter(description = "ID de l'ingrédient à supprimer", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de l'ingrédient ID: {}", context, id);
        try {
            ingredientService.delete(id);
            logger.info("{} Ingrédient ID: {} supprimé avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la suppression de l'ingrédient ID: {} - {}", 
                        context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Rechercher des ingrédients par nom
     */
    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des ingrédients",
            description = "Recherche des ingrédients par nom (insensible à la casse)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Résultats de la recherche",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))
            )
    )
    public ResponseEntity<List<IngredientDto>> searchIngredients(
            @Parameter(
                    description = "Mot-clé de recherche",
                    required = true,
                    example = "Tomate"
            )
            @RequestParam String keyword,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Recherche d'ingrédients avec le mot-clé: {}", context, keyword);
        List<IngredientDto> ingredients = ingredientService.search(keyword);
        logger.info("{} {} ingrédients trouvés pour le mot-clé '{}'", 
                   context, ingredients.size(), keyword);
        return ResponseEntity.ok(ingredients);
    }

    /**
     * Récupérer les ingrédients en alerte
     */
    @GetMapping("/alertes")
    @Operation(
            summary = "Récupérer les ingrédients en alerte",
            description = "Retourne tous les ingrédients dont la quantité actuelle est inférieure ou égale au seuil d'alerte"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des ingrédients en alerte",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))
            )
    )
    public ResponseEntity<List<IngredientDto>> getIngredientsEnAlerte(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des ingrédients en alerte", context);
        List<IngredientDto> ingredients = ingredientService.findIngredientsEnAlerte();
        logger.info("{} {} ingrédients en alerte récupérés", context, ingredients.size());
        return ResponseEntity.ok(ingredients);
    }

    /**
     * Récupérer les ingrédients par unité de mesure
     */
    @GetMapping("/unite/{uniteMesure}")
    @Operation(
            summary = "Récupérer les ingrédients par unité de mesure",
            description = "Retourne tous les ingrédients utilisant une unité de mesure spécifique"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des ingrédients filtrée",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))
            )
    )
    public ResponseEntity<?> getIngredientsByUniteMesure(
            @Parameter(
                    description = "Unité de mesure",
                    required = true,
                    example = "kg"
            )
            @PathVariable String uniteMesure,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des ingrédients par unité de mesure: {}", context, uniteMesure);
        try {
            List<IngredientDto> ingredients = ingredientService.findByUniteMesure(uniteMesure);
            logger.info("{} {} ingrédients trouvés pour l'unité '{}'", 
                       context, ingredients.size(), uniteMesure);
            return ResponseEntity.ok(ingredients);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération des ingrédients par unité {} - {}", 
                        context, uniteMesure, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Ajouter une quantité à un ingrédient
     */
    @PatchMapping("/{id}/ajouter")
    @Operation(
            summary = "Ajouter une quantité à un ingrédient",
            description = "Augmente la quantité actuelle d'un ingrédient (réapprovisionnement)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quantité ajoutée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IngredientDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ingrédient non trouvé"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Quantité invalide"
            )
    })
    public ResponseEntity<?> ajouterQuantite(
            @Parameter(description = "ID de l'ingrédient", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Quantité à ajouter", required = true, example = "10.5")
            @RequestParam BigDecimal quantite,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative d'ajout de quantité - Ingrédient ID: {}, Quantité: {}", 
                   context, id, quantite);
        try {
            IngredientDto updatedIngredient = ingredientService.ajouterQuantite(id, quantite);
            logger.info("{} Quantité ajoutée avec succès - Ingrédient ID: {}, Nouvelle quantité: {}", 
                       context, id, updatedIngredient.getQuantiteActuelle());
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de l'ajout de quantité pour l'ingrédient ID: {} - {}", 
                        context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Méthode utilitaire pour construire les réponses d'erreur
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (e.getMessage().toLowerCase().contains("non trouvé") ||
                e.getMessage().toLowerCase().contains("non trouvée")) {
            status = HttpStatus.NOT_FOUND;
        }

        return ResponseEntity.status(status).body(error);
    }
}