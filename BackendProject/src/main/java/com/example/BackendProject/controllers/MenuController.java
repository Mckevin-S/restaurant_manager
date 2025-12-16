package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.MenuDto;
import com.example.BackendProject.services.implementations.MenuServiceImplementation;
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
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Menus", description = "API pour la gestion des menus principaux du restaurant")
public class MenuController {

    private final MenuServiceImplementation menuServiceImplementation;

    public MenuController(MenuServiceImplementation menuServiceImplementation) {
        this.menuServiceImplementation = menuServiceImplementation;
    }

    /**
     * Créer un nouveau menu
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouveau menu",
            description = "Permet d'ajouter un nouveau menu (ex: Carte d'été, Menu du soir)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Menu créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Nom manquant ou déjà existant")
    })
    public ResponseEntity<MenuDto> createMenu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Détails du menu à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MenuDto.class))
            )
            @RequestBody MenuDto menuDto) {
        try {
            MenuDto savedMenu = menuServiceImplementation.save(menuDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMenu);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Récupérer tous les menus
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les menus",
            description = "Liste tous les menus disponibles dans l'établissement"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MenuDto.class))
            )
    )
    public ResponseEntity<List<MenuDto>> getAllMenus() {
        return ResponseEntity.ok(menuServiceImplementation.getAll());
    }

    /**
     * Récupérer un menu par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un menu par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu trouvé"),
            @ApiResponse(responseCode = "404", description = "Menu non trouvé")
    })
    public ResponseEntity<MenuDto> getMenuById(
            @Parameter(description = "ID du menu", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(menuServiceImplementation.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Mettre à jour un menu
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Menu non trouvé")
    })
    public ResponseEntity<?> updateMenu( @PathVariable Long id, @RequestBody MenuDto menuDto) {
        try {
            return new ResponseEntity<>(menuServiceImplementation.update(id, menuDto), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Supprimer un menu
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un menu",
            description = "Supprime un menu vide. Échoue si des catégories y sont rattachées."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Suppression réussie"),
            @ApiResponse(responseCode = "400", description = "Impossible de supprimer (contient des catégories)"),
            @ApiResponse(responseCode = "404", description = "Menu non trouvé")
    })
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        try {
            menuServiceImplementation.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("catégories")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
