package com.example.BackendProject.controllers;

<<<<<<< HEAD
import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.services.implementations.RestaurantServiceImplementation;
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
@RequestMapping("/restaurants")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Restaurants", description = "API pour la gestion des restaurants")
public class RestaurantController {

    private final RestaurantServiceImplementation restaurantService;

    public RestaurantController(RestaurantServiceImplementation restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Créer un nouveau restaurant
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau restaurant", description = "Ajoute un nouveau restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant créé avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur de validation"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> createRestaurant(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Détails du restaurant à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RestaurantDto.class))
            )
            @RequestBody RestaurantDto restaurantDto) {
        try {
            RestaurantDto created = restaurantService.createRestaurant(restaurantDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur de validation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du restaurant: " + e.getMessage());
        }
    }

    /**
     * Mettre à jour un restaurant
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un restaurant", description = "Met à jour les informations d'un restaurant existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant mis à jour",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantDto.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant non trouvé"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation")
    })
    public ResponseEntity<?> updateRestaurant(
            @Parameter(description = "ID du restaurant", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du restaurant",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RestaurantDto.class))
            )
            @RequestBody RestaurantDto restaurantDto) {
        try {
            RestaurantDto updated = restaurantService.updateRestaurant(id, restaurantDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur de validation: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Restaurant non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    /**
     * Récupérer tous les restaurants
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les restaurants", description = "Liste tous les restaurants disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RestaurantDto.class)))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> getAllRestaurants() {
        try {
            List<RestaurantDto> restaurants = restaurantService.getAllRestaurants();
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des restaurants: " + e.getMessage());
        }
    }

    /**
     * Récupérer un restaurant par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un restaurant par ID", description = "Récupère les détails d'un restaurant spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant trouvé",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantDto.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant non trouvé")
    })
    public ResponseEntity<?> getRestaurantById(
            @Parameter(description = "ID du restaurant", required = true, example = "1")
            @PathVariable Long id) {
        try {
            RestaurantDto dto = restaurantService.getRestaurantById(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Restaurant non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    /**
     * Supprimer un restaurant
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un restaurant", description = "Supprime un restaurant existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurant supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Restaurant non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> deleteRestaurant(
            @Parameter(description = "ID du restaurant à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            restaurantService.deleteRestaurant(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Restaurant non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }
}
=======
public class RestaurantController {
}
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
