package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.CommandePromotionDto;
import com.example.BackendProject.services.implementations.CommandePromotionServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commande-promotions")
@Tag(name = "Gestion Commande-Promotion", description = "API pour gérer l'application de promotions aux commandes")
public class CommandePromotionController {

    private static final Logger logger = LoggerFactory.getLogger(CommandePromotionController.class);
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
            @RequestBody CommandePromotionDto commandePromotionDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative d'association Commande ID: {} avec Promotion ID: {}", 
                context, commandePromotionDto.getCommande(), commandePromotionDto.getPromotion());
        try {
            CommandePromotionDto saved = commandePromotionService.save(commandePromotionDto);
            logger.info("{} Association créée avec succès", context);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la création de l'association: {}", context, e.getMessage());
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
    public ResponseEntity<List<CommandePromotionDto>> getAllCommandePromotions(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les associations commande-promotion", context);
        List<CommandePromotionDto> associations = commandePromotionService.getAll();
        logger.info("{} {} associations récupérées", context, associations.size());
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
            @PathVariable Long promotionId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de l'association Commande: {} - Promotion: {}", context, commandeId, promotionId);
        try {
            CommandePromotionDto association = commandePromotionService.getById(commandeId, promotionId);
            logger.info("{} Association trouvée", context);
            return ResponseEntity.ok(association);
        } catch (RuntimeException e) {
            logger.error("{} Association non trouvée: {}", context, e.getMessage());
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
            @PathVariable Long promotionId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.warn("{} Tentative de suppression de l'association Commande: {} - Promotion: {}", context, commandeId, promotionId);
        try {
            commandePromotionService.delete(commandeId, promotionId);
            logger.info("{} Association supprimée avec succès", context);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la suppression: {}", context, e.getMessage());
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
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des promotions pour la commande ID: {}", context, commandeId);
        try {
            List<CommandePromotionDto> promotions = commandePromotionService.findByCommandeId(commandeId);
            logger.info("{} {} promotions trouvées pour la commande {}", context, promotions.size(), commandeId);
            return ResponseEntity.ok(promotions);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération: {}", context, e.getMessage());
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
            @PathVariable Long promotionId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des commandes pour la promotion ID: {}", context, promotionId);
        try {
            List<CommandePromotionDto> commandes = commandePromotionService.findByPromotionId(promotionId);
            logger.info("{} {} commandes trouvées pour la promotion {}", context, commandes.size(), promotionId);
            return ResponseEntity.ok(commandes);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération: {}", context, e.getMessage());
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
            @PathVariable Long promotionId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Application directe Promotion: {} -> Commande: {}", context, promotionId, commandeId);
        try {
            CommandePromotionDto result = commandePromotionService.appliquerPromotion(commandeId, promotionId);
            logger.info("{} Promotion appliquée avec succès", context);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de l'application de la promotion: {}", context, e.getMessage());
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
            @PathVariable Long promotionId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Retrait Promotion: {} de la Commande: {}", context, promotionId, commandeId);
        try {
            commandePromotionService.retirerPromotion(commandeId, promotionId);
            logger.info("{} Promotion retirée avec succès", context);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors du retrait de la promotion: {}", context, e.getMessage());
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
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.warn("{} Retrait de TOUTES les promotions pour la commande ID: {}", context, commandeId);
        try {
            commandePromotionService.retirerToutesPromotions(commandeId);
            logger.info("{} Toutes les promotions ont été retirées avec succès", context);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors du retrait groupé: {}", context, e.getMessage());
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
            @PathVariable Long promotionId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Vérification promotion: {} sur commande: {}", context, promotionId, commandeId);
        boolean estAppliquee = commandePromotionService.promotionEstAppliquee(commandeId, promotionId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("promotionAppliquee", estAppliquee);
        
        logger.info("{} Résultat de la vérification: {}", context, estAppliquee);
        return ResponseEntity.ok(response);
    }
}