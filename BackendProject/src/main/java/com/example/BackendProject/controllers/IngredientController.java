package com.example.BackendProject.controllers;



import com.example.BackendProject.dto.IngredientDto;
import com.example.BackendProject.services.implementations.IngredientServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
            @RequestBody IngredientDto ingredientDto) {
        try {
            IngredientDto savedIngredient = ingredientService.save(ingredientDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedIngredient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        List<IngredientDto> ingredients = ingredientService.getAll();
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
            @PathVariable Long id) {
        try {
            IngredientDto ingredient = ingredientService.getById(id);
            return ResponseEntity.ok(ingredient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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
            @RequestBody IngredientDto ingredientDto) {
        try {
            IngredientDto updatedIngredient = ingredientService.update(id, ingredientDto);
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
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
            @PathVariable Long id) {
        try {
            ingredientService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
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
            @RequestParam String keyword) {
        List<IngredientDto> ingredients = ingredientService.search(keyword);
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
    public ResponseEntity<List<IngredientDto>> getIngredientsEnAlerte() {
        List<IngredientDto> ingredients = ingredientService.findIngredientsEnAlerte();
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
            @PathVariable String uniteMesure) {
        try {
            List<IngredientDto> ingredients = ingredientService.findByUniteMesure(uniteMesure);
            return ResponseEntity.ok(ingredients);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
            @RequestParam BigDecimal quantite) {
        try {
            IngredientDto updatedIngredient = ingredientService.ajouterQuantite(id, quantite);
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Retirer une quantité d'un ingrédient
     */
    @PatchMapping("/{id}/retirer")
    @Operation(
            summary = "Retirer une quantité d'un ingrédient",
            description = "Diminue la quantité actuelle d'un ingrédient (consommation)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quantité retirée avec succès",
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
                    description = "Quantité insuffisante ou invalide",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Quantité insuffisante. Stock actuel : 5 kg\"}")
                    )
            )
    })
    public ResponseEntity<?> retirerQuantite(
            @Parameter(description = "ID de l'ingrédient", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Quantité à retirer", required = true, example = "2.5")
            @RequestParam BigDecimal quantite) {
        try {
            IngredientDto updatedIngredient = ingredientService.retirerQuantite(id, quantite);
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Mettre à jour le seuil d'alerte
     */
    @PatchMapping("/{id}/seuil-alerte")
    @Operation(
            summary = "Mettre à jour le seuil d'alerte",
            description = "Modifie le seuil d'alerte d'un ingrédient"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Seuil d'alerte mis à jour avec succès",
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
                    description = "Seuil invalide"
            )
    })
    public ResponseEntity<?> updateSeuilAlerte(
            @Parameter(description = "ID de l'ingrédient", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouveau seuil d'alerte", required = true, example = "5.0")
            @RequestParam BigDecimal seuil) {
        try {
            IngredientDto updatedIngredient = ingredientService.updateSeuilAlerte(id, seuil);
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Obtenir les statistiques du stock
     */
    @GetMapping("/statistiques")
    @Operation(
            summary = "Obtenir les statistiques du stock",
            description = "Retourne des statistiques sur le stock d'ingrédients"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Statistiques récupérées avec succès"
    )
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        List<IngredientDto> tous = ingredientService.getAll();
        List<IngredientDto> enAlerte = ingredientService.findIngredientsEnAlerte();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIngredients", tous.size());
        stats.put("ingredientsEnAlerte", enAlerte.size());
        stats.put("tauxAlerte", tous.isEmpty() ? 0 : (enAlerte.size() * 100.0 / tous.size()));

        return ResponseEntity.ok(stats);
    }
}
