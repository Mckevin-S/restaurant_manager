package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.services.implementations.RestaurantServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("api/restaurants")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Restaurants", description = "API pour la gestion des restaurants")
public class RestaurantController {

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
    public ResponseEntity<RestaurantDto> createRestaurant(@RequestBody RestaurantDto restaurantDto) {
        // Le try-catch peut être géré par un GlobalExceptionHandler pour alléger le contrôleur
        RestaurantDto created = restaurantService.createRestaurant(restaurantDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un restaurant")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDto restaurantDto) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, restaurantDto));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les restaurants")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantDto.class))))
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un restaurant par ID")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un restaurant")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
    }
}