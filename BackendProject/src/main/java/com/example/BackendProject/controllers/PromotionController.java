package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.PromotionDto;
import com.example.BackendProject.services.implementations.PromotionServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PromotionController.class);
    private final PromotionServiceImplementation service;

    @Operation(summary = "Créer une nouvelle promotion", description = "Permet d'ajouter une promotion en base de données")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promotion créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    public ResponseEntity<PromotionDto> create(@RequestBody PromotionDto dto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'une promotion", context);
        try {
            PromotionDto savedPromotion = service.save(dto);
            logger.info("{} Promotion créée avec succès. ID: {}", context, savedPromotion.getId());
            return new ResponseEntity<>(savedPromotion, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création de la promotion: {}", context, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Récupérer toutes les promotions", description = "Retourne la liste complète des promotions (actives ou non)")
    @GetMapping
    public ResponseEntity<List<PromotionDto>> getAll(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les promotions", context);
        List<PromotionDto> promotions = service.findAll();
        logger.info("{} {} promotions récupérées avec succès", context, promotions.size());
        return ResponseEntity.ok(promotions);
    }

    @Operation(summary = "Trouver une promotion par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promotion trouvée"),
            @ApiResponse(responseCode = "404", description = "Promotion non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDto> getOne(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la promotion avec l'ID: {}", context, id);
        try {
            PromotionDto promotion = service.findById(id);
            logger.info("{} Promotion ID: {} récupérée avec succès", context, id);
            return ResponseEntity.ok(promotion);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération de la promotion ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Mettre à jour une promotion existante")
    @PutMapping("/{id}")
    public ResponseEntity<PromotionDto> update(@PathVariable Long id, @RequestBody PromotionDto dto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour de la promotion ID: {}", context, id);
        try {
            PromotionDto updatedPromotion = service.update(id, dto);
            logger.info("{} Promotion ID: {} mise à jour avec succès", context, id);
            return ResponseEntity.ok(updatedPromotion);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour de la promotion ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Supprimer une promotion")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de la promotion ID: {}", context, id);
        try {
            service.delete(id);
            logger.info("{} Promotion ID: {} supprimée avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression de la promotion ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }
}