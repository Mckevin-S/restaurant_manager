package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.PromotionDto;
import com.example.BackendProject.services.implementations.PromotionServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Promotion", description = "API de gestion des promotions et réductions")
public class PromotionController {

    private final PromotionServiceImplementation service;

    @Operation(summary = "Créer une nouvelle promotion", description = "Permet d'ajouter une promotion en base de données")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promotion créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    public ResponseEntity<PromotionDto> create(@RequestBody PromotionDto dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Récupérer toutes les promotions", description = "Retourne la liste complète des promotions (actives ou non)")
    @GetMapping
    public ResponseEntity<List<PromotionDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Trouver une promotion par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promotion trouvée"),
            @ApiResponse(responseCode = "404", description = "Promotion non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Mettre à jour une promotion existante")
    @PutMapping("/{id}")
    public ResponseEntity<PromotionDto> update(@PathVariable Long id, @RequestBody PromotionDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Supprimer une promotion")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}