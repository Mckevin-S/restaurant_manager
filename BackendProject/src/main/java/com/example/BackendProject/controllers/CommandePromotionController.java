package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.CommandePromotionDto;
import com.example.BackendProject.services.implementations.CommandePromotionServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commande-promotions")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion Commande-Promotion", description = "API pour gérer l'application de promotions aux commandes")
public class CommandePromotionController {

    private final CommandePromotionServiceImplementation commandePromotionService;

    public CommandePromotionController(CommandePromotionServiceImplementation commandePromotionService) {
        this.commandePromotionService = commandePromotionService;
    }


    /**
     * Créer une association commande-promotion
     */
    @PostMapping
    @Operation(
            summary = "Créer une association commande-promotion",
            description = "Applique une promotion à une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Promotion appliquée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommandePromotionDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Promotion déjà appliquée, expirée ou inactive",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Cette promotion est déjà appliquée à cette commande\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande ou promotion non trouvée"
            )
    })
    public ResponseEntity<?> createCommandePromotion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Association commande-promotion à créer",
                    required = true
            )
            @RequestBody CommandePromotionDto commandePromotionDto) {
        try {
            CommandePromotionDto saved = commandePromotionService.save(commandePromotionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvée") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Récupérer toutes les associations
     */
    @GetMapping
    @Operation(
            summary = "Récupérer toutes les associations commande-promotion",
            description = "Retourne la liste complète de toutes les associations"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommandePromotionDto.class))
            )
    )
    public ResponseEntity<List<CommandePromotionDto>> getAllCommandePromotions() {
        List<CommandePromotionDto> associations = commandePromotionService.getAll();
        return ResponseEntity.ok(associations);
    }

    /**
     * Récupérer une association spécifique
     */
    @GetMapping("/commande/{commandeId}/promotion/{promotionId}")
    @Operation(
            summary = "Récupérer une association spécifique",
            description = "Retourne les détails d'une association commande-promotion"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Association trouvée"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Association non trouvée"
            )
    })
    public ResponseEntity<?> getCommandePromotion(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            @Parameter(description = "ID de la promotion", required = true, example = "1")
            @PathVariable Long promotionId) {
        try {
            CommandePromotionDto association = commandePromotionService.getById(commandeId, promotionId);
            return ResponseEntity.ok(association);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Supprimer une association
     */
    @DeleteMapping("/commande/{commandeId}/promotion/{promotionId}")
    @Operation(
            summary = "Supprimer une association commande-promotion",
            description = "Retire une promotion d'une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Promotion retirée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Association non trouvée"
            )
    })
    public ResponseEntity<?> deleteCommandePromotion(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            @Parameter(description = "ID de la promotion", required = true, example = "1")
            @PathVariable Long promotionId) {
        try {
            commandePromotionService.delete(commandeId, promotionId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Récupérer toutes les promotions d'une commande
     */
    @GetMapping("/commande/{commandeId}")
    @Operation(
            summary = "Récupérer les promotions d'une commande",
            description = "Retourne toutes les promotions appliquées à une commande spécifique"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des promotions de la commande",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommandePromotionDto.class))
            )
    )
    public ResponseEntity<?> getPromotionsByCommande(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId) {
        try {
            List<CommandePromotionDto> promotions = commandePromotionService.findByCommandeId(commandeId);
            return ResponseEntity.ok(promotions);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Récupérer toutes les commandes ayant une promotion
     */
    @GetMapping("/promotion/{promotionId}")
    @Operation(
            summary = "Récupérer les commandes ayant une promotion",
            description = "Retourne toutes les commandes auxquelles une promotion spécifique a été appliquée"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des commandes avec cette promotion",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommandePromotionDto.class))
            )
    )
    public ResponseEntity<?> getCommandesByPromotion(
            @Parameter(description = "ID de la promotion", required = true, example = "1")
            @PathVariable Long promotionId) {
        try {
            List<CommandePromotionDto> commandes = commandePromotionService.findByPromotionId(promotionId);
            return ResponseEntity.ok(commandes);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Appliquer une promotion à une commande
     */
    @PostMapping("/commande/{commandeId}/appliquer/{promotionId}")
    @Operation(
            summary = "Appliquer une promotion à une commande",
            description = "Méthode simplifiée pour appliquer une promotion à une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Promotion appliquée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Promotion déjà appliquée, expirée ou inactive"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande ou promotion non trouvée"
            )
    })
    public ResponseEntity<?> appliquerPromotion(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            @Parameter(description = "ID de la promotion à appliquer", required = true, example = "1")
            @PathVariable Long promotionId) {
        try {
            CommandePromotionDto result = commandePromotionService.appliquerPromotion(commandeId, promotionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvée") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Retirer une promotion d'une commande
     */
    @DeleteMapping("/commande/{commandeId}/retirer/{promotionId}")
    @Operation(
            summary = "Retirer une promotion d'une commande",
            description = "Méthode simplifiée pour retirer une promotion d'une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Promotion retirée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Association non trouvée"
            )
    })
    public ResponseEntity<?> retirerPromotion(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            @Parameter(description = "ID de la promotion à retirer", required = true, example = "1")
            @PathVariable Long promotionId) {
        try {
            commandePromotionService.retirerPromotion(commandeId, promotionId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Retirer toutes les promotions d'une commande
     */
    @DeleteMapping("/commande/{commandeId}/retirer-toutes")
    @Operation(
            summary = "Retirer toutes les promotions d'une commande",
            description = "Supprime toutes les promotions appliquées à une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Toutes les promotions ont été retirées"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            )
    })
    public ResponseEntity<?> retirerToutesPromotions(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId) {
        try {
            commandePromotionService.retirerToutesPromotions(commandeId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Vérifier si une promotion est appliquée
     */
    @GetMapping("/commande/{commandeId}/promotion/{promotionId}/verifier")
    @Operation(
            summary = "Vérifier si une promotion est appliquée",
            description = "Vérifie si une promotion est actuellement appliquée à une commande"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Résultat de la vérification"
    )
    public ResponseEntity<Map<String, Boolean>> verifierPromotion(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            @Parameter(description = "ID de la promotion", required = true, example = "1")
            @PathVariable Long promotionId) {
        boolean estAppliquee = commandePromotionService.promotionEstAppliquee(commandeId, promotionId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("promotionAppliquee", estAppliquee);

        return ResponseEntity.ok(response);
    }
}
