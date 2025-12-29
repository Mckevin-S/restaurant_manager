package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.services.implementations.CuisineServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuisine")
@PreAuthorize("hasAnyRole('CUISINIER', 'MANAGER')")
public class CuisineController {

    private final CuisineServiceImplementation cuisineService;

    public CuisineController(CuisineServiceImplementation cuisineService) {
        this.cuisineService = cuisineService;
    }

    @GetMapping("/en-cours")
    @Operation(summary = "Liste des commandes à préparer (EN_ATTENTE & EN_PREPARATION)")
    public ResponseEntity<List<CommandeDto>> getCommandesCuisine() {
        return ResponseEntity.ok(cuisineService.getListeAPreparer());
    }

    @PatchMapping("/{id}/commencer")
    @Operation(summary = "Le cuisinier commence la préparation")
    public ResponseEntity<CommandeDto> commencer(@PathVariable Long id) {
        return ResponseEntity.ok(cuisineService.commencerPreparation(id));
    }

    @PatchMapping("/{id}/terminer")
    @Operation(summary = "La commande est prête à être servie")
    public ResponseEntity<CommandeDto> terminer(@PathVariable Long id) {
        return ResponseEntity.ok(cuisineService.marquerCommePrete(id));
    }
}
