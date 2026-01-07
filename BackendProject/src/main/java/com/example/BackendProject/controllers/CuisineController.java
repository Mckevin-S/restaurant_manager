package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.services.implementations.CuisineServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuisine")
@PreAuthorize("hasAnyRole('CUISINIER', 'MANAGER')")
@Tag(name = "Gestion de la Cuisine", description = "API pour la gestion des commandes en cuisine")
public class CuisineController {

    private static final Logger logger = LoggerFactory.getLogger(CuisineController.class);
    private final CuisineServiceImplementation cuisineService;

    public CuisineController(CuisineServiceImplementation cuisineService) {
        this.cuisineService = cuisineService;
    }

    /**
     * Récupérer les commandes en cours de préparation
     */
    @GetMapping("/en-cours")
    @Operation(
            summary = "Liste des commandes à préparer",
            description = "Retourne toutes les commandes avec le statut EN_ATTENTE ou EN_PREPARATION"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des commandes en cours récupérée avec succès"
    )
    public ResponseEntity<List<CommandeDto>> getCommandesCuisine(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des commandes en cours de préparation", context);
        List<CommandeDto> commandes = cuisineService.getListeAPreparer();
        logger.info("{} {} commandes en cours récupérées avec succès", context, commandes.size());
        return ResponseEntity.ok(commandes);
    }

    /**
     * Commencer la préparation d'une commande
     */
    @PatchMapping("/{id}/commencer")
    @Operation(
            summary = "Commencer la préparation",
            description = "Le cuisinier commence la préparation d'une commande (passage au statut EN_PREPARATION)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Préparation commencée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Commande dans un état invalide pour cette opération"
            )
    })
    public ResponseEntity<?> commencer(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de début de préparation pour la commande ID: {}", context, id);
        try {
            CommandeDto commande = cuisineService.commencerPreparation(id);
            logger.info("{} Préparation commencée avec succès pour la commande ID: {} - Statut: {}", 
                       context, id, commande.getStatut());
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors du début de préparation de la commande ID: {} - {}", 
                        context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Terminer la préparation d'une commande
     */
    @PatchMapping("/{id}/terminer")
    @Operation(
            summary = "Terminer la préparation",
            description = "Marque la commande comme prête à être servie (passage au statut PRETE)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Commande marquée comme prête avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Commande dans un état invalide pour cette opération"
            )
    })
    public ResponseEntity<?> terminer(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de finalisation de préparation pour la commande ID: {}", context, id);
        try {
            CommandeDto commande = cuisineService.marquerCommePrete(id);
            logger.info("{} Préparation terminée avec succès pour la commande ID: {} - Statut: {}", 
                       context, id, commande.getStatut());
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la finalisation de préparation de la commande ID: {} - {}", 
                        context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Méthode utilitaire pour construire les réponses d'erreur
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (e.getMessage().toLowerCase().contains("non trouvé") ||
                e.getMessage().toLowerCase().contains("non trouvée")) {
            status = HttpStatus.NOT_FOUND;
        }

        return ResponseEntity.status(status).body(error);
    }
}