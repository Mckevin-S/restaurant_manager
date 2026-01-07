package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.MenuDto;
import com.example.BackendProject.services.implementations.MenuServiceImplementation;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Menus", description = "API pour la gestion des menus principaux du restaurant")
public class MenuController {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
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
            @RequestBody MenuDto menuDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'un menu", context);
        try {
            MenuDto savedMenu = menuServiceImplementation.save(menuDto);
            logger.info("{} Menu créé avec succès. ID: {}", context, savedMenu.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMenu);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création du menu: {}", context, e.getMessage(), e);
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
    public ResponseEntity<List<MenuDto>> getAllMenus(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de tous les menus", context);
        List<MenuDto> menus = menuServiceImplementation.getAll();
        logger.info("{} {} menus récupérés avec succès", context, menus.size());
        return ResponseEntity.ok(menus);
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
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération du menu avec l'ID: {}", context, id);
        try {
            MenuDto menu = menuServiceImplementation.getById(id);
            logger.info("{} Menu ID: {} récupéré avec succès", context, id);
            return ResponseEntity.ok(menu);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération du menu ID: {} - {}", context, id, e.getMessage(), e);
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
    public ResponseEntity<?> updateMenu(@PathVariable Long id, @RequestBody MenuDto menuDto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour du menu ID: {}", context, id);
        try {
            MenuDto updatedMenu = menuServiceImplementation.update(id, menuDto);
            logger.info("{} Menu ID: {} mis à jour avec succès", context, id);
            return new ResponseEntity<>(updatedMenu, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la mise à jour du menu ID: {} - {}", context, id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression du menu ID: {}", context, id);
        try {
            menuServiceImplementation.delete(id);
            logger.info("{} Menu ID: {} supprimé avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la suppression du menu ID: {} - {}", context, id, e.getMessage(), e);
            if (e.getMessage().contains("catégories")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
