package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.services.implementations.RestaurantServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping({"api/restaurants", "api/restaurant"})
@Tag(name = "Gestion des Restaurants", description = "API pour la gestion des restaurants")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
    // Injection via l'interface (meilleure pratique)
    private final RestaurantServiceImplementation restaurantService;

    public RestaurantController(RestaurantServiceImplementation restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<RestaurantDto> createRestaurant(@RequestBody RestaurantDto restaurantDto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'un restaurant", context);
        try {
            RestaurantDto created = restaurantService.createRestaurant(restaurantDto);
            logger.info("{} Restaurant créé avec succès. ID: {}", context, created.getId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création du restaurant: {}", context, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un restaurant")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDto restaurantDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour du restaurant ID: {}", context, id);
        try {
            RestaurantDto updated = restaurantService.updateRestaurant(id, restaurantDto);
            logger.info("{} Restaurant ID: {} mis à jour avec succès", context, id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour du restaurant ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les restaurants")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantDto.class))))
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de tous les restaurants", context);
        List<RestaurantDto> restaurants = restaurantService.getAllRestaurants();
        logger.info("{} {} restaurants récupérés avec succès", context, restaurants.size());
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un restaurant par ID")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération du restaurant avec l'ID: {}", context, id);
        try {
            RestaurantDto restaurant = restaurantService.getRestaurantById(id);
            logger.info("{} Restaurant ID: {} récupéré avec succès", context, id);
            return ResponseEntity.ok(restaurant);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération du restaurant ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/settings")
    @Operation(summary = "Récupérer les paramètres globaux du restaurant")
    public ResponseEntity<RestaurantDto> getSettings(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des paramètres du restaurant", context);
        return ResponseEntity.ok(restaurantService.getSettings());
    }

    @PutMapping("/settings")
    @Operation(summary = "Mettre à jour les paramètres globaux du restaurant")
    public ResponseEntity<RestaurantDto> updateSettings(
            @RequestBody RestaurantDto restaurantDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Mise à jour des paramètres du restaurant", context);
        try {
            RestaurantDto updated = restaurantService.updateSettings(restaurantDto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("{} Erreur settings: {}", context, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un restaurant")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression du restaurant ID: {}", context, id);
        try {
            restaurantService.deleteRestaurant(id);
            logger.info("{} Restaurant ID: {} supprimé avec succès", context, id);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression du restaurant ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping(value = "/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader et redimensionner le logo du restaurant")
    public ResponseEntity<?> uploadLogo(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative d'upload de logo - Nom du fichier: {}", context, file.getOriginalFilename());
        try {
            RestaurantDto updatedRestaurant = restaurantService.uploadLogo(file);
            logger.info("{} Logo uploadé avec succès - URL: {}", context, updatedRestaurant.getLogo());
            return ResponseEntity.ok(updatedRestaurant);
        } catch (IOException e) {
            logger.error("{} Erreur IO lors de l'upload du logo - {}", context, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement du logo : " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de l'upload du logo - {}", context, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}