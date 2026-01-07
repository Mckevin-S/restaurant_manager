package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.PaiementDto;
import com.example.BackendProject.services.implementations.PaiementServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.TypePaiement;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des paiements.
 * Intègre la documentation Swagger et la gestion des erreurs.
 */
@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Paiements", description = "API pour gérer les paiements des commandes")
public class PaiementController {

    private static final Logger logger = LoggerFactory.getLogger(PaiementController.class);
    private final PaiementServiceImplementation paiementService;

    public PaiementController(PaiementServiceImplementation paiementService) {
        this.paiementService = paiementService;
    }

    /**
     * Créer un nouveau paiement
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouveau paiement",
            description = "Enregistre un paiement pour une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Paiement créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaiementDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides ou commande déjà payée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Cette commande a déjà été payée\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            )
    })
    public ResponseEntity<?> createPaiement(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Paiement à créer",
                    required = true
            )
            @RequestBody PaiementDto paiementDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'un paiement pour la commande ID: {}", 
                    context, paiementDto.getCommande() != null ? paiementDto.getCommande() : "N/A");
        try {
            PaiementDto saved = paiementService.save(paiementDto);
            logger.info("{} Paiement créé avec succès. ID: {} - Montant: {}", context, saved.getId(), saved.getMontant());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la création du paiement: {}", context, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Récupérer tous les paiements
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les paiements",
            description = "Retourne la liste complète de tous les paiements"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PaiementDto.class))
            )
    )
    public ResponseEntity<List<PaiementDto>> getAllPaiements(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de tous les paiements", context);
        List<PaiementDto> paiements = paiementService.getAll();
        logger.info("{} {} paiements récupérés avec succès", context, paiements.size());
        return ResponseEntity.ok(paiements);
    }

    /**
     * Récupérer un paiement par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un paiement par ID",
            description = "Retourne les détails d'un paiement spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<?> getPaiementById(
            @Parameter(description = "ID du paiement", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération du paiement avec l'ID: {}", context, id);
        try {
            PaiementDto paiement = paiementService.getById(id);
            logger.info("{} Paiement ID: {} récupéré avec succès", context, id);
            return ResponseEntity.ok(paiement);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération du paiement ID: {} - {}", context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Mettre à jour un paiement
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un paiement",
            description = "Met à jour les informations d'un paiement"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<?> updatePaiement(
            @Parameter(description = "ID du paiement", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody PaiementDto paiementDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour du paiement ID: {}", context, id);
        try {
            PaiementDto updated = paiementService.update(id, paiementDto);
            logger.info("{} Paiement ID: {} mis à jour avec succès", context, id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la mise à jour du paiement ID: {} - {}", context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Supprimer un paiement
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un paiement",
            description = "Annule un paiement et remet la commande en attente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Paiement supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<?> deletePaiement(
            @Parameter(description = "ID du paiement", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression du paiement ID: {}", context, id);
        try {
            paiementService.delete(id);
            logger.info("{} Paiement ID: {} supprimé avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la suppression du paiement ID: {} - {}", context, id, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Récupérer les paiements d'une commande
     */
    @GetMapping("/commande/{commandeId}")
    @Operation(
            summary = "Récupérer les paiements d'une commande",
            description = "Retourne tous les paiements associés à une commande"
    )
    @ApiResponse(responseCode = "200", description = "Liste des paiements de la commande")
    public ResponseEntity<?> getPaiementsByCommande(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des paiements pour la commande ID: {}", context, commandeId);
        try {
            List<PaiementDto> paiements = paiementService.findByCommandeId(commandeId);
            logger.info("{} {} paiements récupérés pour la commande ID: {}", context, paiements.size(), commandeId);
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération des paiements pour la commande ID: {} - {}", context, commandeId, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Récupérer les paiements par type
     */
    @GetMapping("/type/{typePaiement}")
    @Operation(
            summary = "Récupérer les paiements par type",
            description = "Retourne tous les paiements d'un type spécifique"
    )
    public ResponseEntity<List<PaiementDto>> getPaiementsByType(
            @Parameter(description = "Type de paiement", required = true)
            @PathVariable TypePaiement typePaiement,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des paiements par type: {}", context, typePaiement);
        List<PaiementDto> paiements = paiementService.findByTypePaiement(typePaiement);
        logger.info("{} {} paiements de type {} récupérés avec succès", context, paiements.size(), typePaiement);
        return ResponseEntity.ok(paiements);
    }

    /**
     * Récupérer un paiement par référence
     */
    @GetMapping("/reference/{reference}")
    @Operation(
            summary = "Récupérer un paiement par référence",
            description = "Recherche un paiement par sa référence de transaction"
    )
    public ResponseEntity<?> getPaiementByReference(
            @Parameter(description = "Référence de transaction", required = true)
            @PathVariable String reference,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération du paiement par référence: {}", context, reference);
        try {
            PaiementDto paiement = paiementService.findByReference(reference);
            logger.info("{} Paiement avec référence {} récupéré avec succès. ID: {}", context, reference, paiement.getId());
            return ResponseEntity.ok(paiement);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération du paiement par référence {} - {}", context, reference, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Effectuer un paiement (méthode simplifiée)
     */
    @PostMapping("/effectuer")
    @Operation(
            summary = "Effectuer un paiement",
            description = "Méthode simplifiée pour enregistrer un paiement"
    )
    public ResponseEntity<?> effectuerPaiement(
            @RequestParam Long commandeId,
            @RequestParam BigDecimal montant,
            @RequestParam TypePaiement typePaiement,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative d'effectuer un paiement - Commande ID: {}, Montant: {}, Type: {}", context, commandeId, montant, typePaiement);
        try {
            PaiementDto paiement = paiementService.effectuerPaiement(commandeId, montant, typePaiement);
            logger.info("{} Paiement effectué avec succès. ID: {} - Commande ID: {}", context, paiement.getId(), commandeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(paiement);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de l'effectuation du paiement pour la commande ID: {} - {}", context, commandeId, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Récupérer les paiements du jour
     */
    @GetMapping("/aujourd-hui")
    @Operation(
            summary = "Récupérer les paiements du jour",
            description = "Retourne tous les paiements effectués aujourd'hui"
    )
    public ResponseEntity<List<PaiementDto>> getPaiementsAujourdhui(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des paiements du jour", context);
        List<PaiementDto> paiements = paiementService.getPaiementsAujourdhui();
        logger.info("{} {} paiements du jour récupérés avec succès", context, paiements.size());
        return ResponseEntity.ok(paiements);
    }

    /**
     * Calculer le total des paiements sur une période
     */
    @GetMapping("/statistiques/total")
    @Operation(
            summary = "Calculer le total des paiements",
            description = "Retourne le montant total des paiements sur une période donnée"
    )
    public ResponseEntity<?> calculateTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Calcul du total des paiements - Période: {} à {}", context, debut, fin);
        try {
            Timestamp timestampDebut = Timestamp.valueOf(debut);
            Timestamp timestampFin = Timestamp.valueOf(fin);
            BigDecimal total = paiementService.calculateTotalByPeriod(timestampDebut, timestampFin);
            logger.info("{} Total calculé avec succès: {}", context, total);

            Map<String, Object> response = new HashMap<>();
            response.put("debut", debut);
            response.put("fin", fin);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors du calcul du total des paiements - {}", context, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Calculer le total par type sur une période
     */
    @GetMapping("/statistiques/total-par-type")
    @Operation(
            summary = "Calculer le total par type",
            description = "Retourne le montant total par type de paiement sur une période donnée"
    )
    public ResponseEntity<?> calculateTotalByType(
            @RequestParam TypePaiement type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Calcul du total des paiements par type - Type: {}, Période: {} à {}", context, type, debut, fin);
        try {
            Timestamp timestampDebut = Timestamp.valueOf(debut);
            Timestamp timestampFin = Timestamp.valueOf(fin);
            BigDecimal total = paiementService.calculateTotalByTypeAndPeriod(type, timestampDebut, timestampFin);
            logger.info("{} Total calculé pour le type {}: {}", context, type, total);

            Map<String, Object> response = new HashMap<>();
            response.put("type", type);
            response.put("debut", debut);
            response.put("fin", fin);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors du calcul du total par type {} - {}", context, type, e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    /**
     * Vérifier si une commande est payée
     */
    @GetMapping("/commande/{commandeId}/statut-paiement")
    @Operation(
            summary = "Vérifier statut paiement",
            description = "Vérifie si une commande spécifique a été payée"
    )
    public ResponseEntity<Boolean> isCommandePayee(@PathVariable Long commandeId, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Vérification du statut de paiement pour la commande ID: {}", context, commandeId);
        boolean estPayee = paiementService.commandeEstPayee(commandeId);
        logger.info("{} Statut de paiement vérifié pour la commande ID: {} - Payée: {}", context, commandeId, estPayee);
        return ResponseEntity.ok(estPayee);
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
