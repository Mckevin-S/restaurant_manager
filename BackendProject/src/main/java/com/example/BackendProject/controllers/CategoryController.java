package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.CategoryDto;
import com.example.BackendProject.services.implementations.CategoryServiceImplementation;
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
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Catégories", description = "API pour la gestion des catégories de menu du restaurant")
public class CategoryController {

    private final CategoryServiceImplementation categoryServiceImplementation;

    public CategoryController(CategoryServiceImplementation categoryServiceImplementation) {
        this.categoryServiceImplementation = categoryServiceImplementation;
    }

    /**
     * Créer une nouvelle catégorie
     */
    @PostMapping
    @Operation(
            summary = "Créer une nouvelle catégorie",
            description = "Permet de créer une nouvelle catégorie associée à un menu"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Catégorie créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides ou menu non trouvé"
            )
    })
    public ResponseEntity<CategoryDto> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de la catégorie à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryDto.class))
            )
            @RequestBody CategoryDto categoryDto) {
        try {
            CategoryDto savedCategory = categoryServiceImplementation.save(categoryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Récupérer toutes les catégories
     */
    @GetMapping
    @Operation(
            summary = "Récupérer toutes les catégories",
            description = "Retourne la liste complète de toutes les catégories enregistrées"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des catégories récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))
            )
    )
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryServiceImplementation.getAll();
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupérer une catégorie par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer une catégorie par ID",
            description = "Retourne les détails d'une catégorie spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Catégorie trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Catégorie non trouvée"
            )
    })
    public ResponseEntity<CategoryDto> getCategoryById(
            @Parameter(description = "ID de la catégorie", required = true, example = "1")
            @PathVariable Long id) {
        try {
            CategoryDto category = categoryServiceImplementation.getById(id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Mettre à jour une catégorie
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une catégorie",
            description = "Met à jour les informations d'une catégorie existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Catégorie mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Catégorie non trouvée"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erreur de validation"
            )
    })
    public ResponseEntity<CategoryDto> updateCategory(
            @Parameter(description = "ID de la catégorie à modifier", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de la catégorie",
                    required = true
            )
            @RequestBody CategoryDto categoryDto) {
        try {
            CategoryDto updatedCategory = categoryServiceImplementation.update(id, categoryDto);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Supprimer une catégorie
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une catégorie",
            description = "Supprime une catégorie si elle ne contient pas de plats liés"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Catégorie supprimée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Impossible de supprimer (contient des plats)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Catégorie non trouvée"
            )
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID de la catégorie à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            categoryServiceImplementation.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("contient des plats")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Rechercher des catégories par ID de menu
     */
    @GetMapping("/menu/{menuId}")
    @Operation(
            summary = "Récupérer les catégories d'un menu",
            description = "Retourne toutes les catégories appartenant à un menu spécifique, triées par ordre d'affichage"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des catégories récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Menu non trouvé"
            )
    })
    public ResponseEntity<List<CategoryDto>> getCategoriesByMenu(
            @Parameter(description = "ID du menu parent", required = true, example = "1")
            @PathVariable Long menuId) {
        try {
            List<CategoryDto> categories = categoryServiceImplementation.findByMenuId(menuId);
            return ResponseEntity.ok(categories);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Rechercher des catégories par nom
     */
    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des catégories",
            description = "Recherche des catégories par nom (partiel et insensible à la casse)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Résultats de la recherche",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))
            )
    )
    public ResponseEntity<List<CategoryDto>> searchCategories(
            @Parameter(description = "Mot-clé de recherche", required = true, example = "Entrées")
            @RequestParam String keyword) {
        List<CategoryDto> categories = categoryServiceImplementation.search(keyword);
        return ResponseEntity.ok(categories);
    }

    /**
     * Réorganiser les catégories
     */
    @PatchMapping("/reorder/{menuId}")
    @Operation(
            summary = "Réorganiser les catégories",
            description = "Met à jour l'ordre d'affichage des catégories pour un menu donné"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réorganisation réussie"),
            @ApiResponse(responseCode = "400", description = "Données ou IDs de catégorie invalides")
    })
    public ResponseEntity<Void> reorderCategories(
            @Parameter(description = "ID du menu", required = true) @PathVariable Long menuId,
            @RequestBody List<Long> categoryIds) {
        try {
            categoryServiceImplementation.reorderCategories(menuId, categoryIds);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}