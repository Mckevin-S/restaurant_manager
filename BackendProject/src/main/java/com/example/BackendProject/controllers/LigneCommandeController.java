package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.LigneCommandeDto;
import com.example.BackendProject.services.implementations.LigneCommandeServiceImplementation;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ligne-commandes")
@Tag(name = "Gestion des Lignes de Commande", description = "API pour gérer les lignes de commande (articles commandés)")
public class LigneCommandeController {

    private static final Logger logger = LoggerFactory.getLogger(LigneCommandeController.class);
    private final LigneCommandeServiceImplementation ligneCommandeService;

    public LigneCommandeController(LigneCommandeServiceImplementation ligneCommandeService) {
        this.ligneCommandeService = ligneCommandeService;
    }


    /**
     * Créer une nouvelle ligne de commande
     */
    @PostMapping
    @Operation(
            summary = "Créer une nouvelle ligne de commande",
            description = "Ajoute un article (plat) à une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ligne de commande créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LigneCommandeDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides ou plat non disponible",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Le plat n'est pas disponible actuellement\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande ou plat non trouvé"
            )
    })
    public ResponseEntity<?> createLigneCommande(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ligne de commande à créer",
                    required = true
            )
            @RequestBody LigneCommandeDto ligneCommandeDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'une ligne de commande pour la commande ID: {}", 
                    context, ligneCommandeDto.getCommande() != null ? ligneCommandeDto.getCommande() : "N/A");
        try {
            LigneCommandeDto saved = ligneCommandeService.save(ligneCommandeDto);
            logger.info("{} Ligne de commande créée avec succès. ID: {}", context, saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la création de la ligne de commande: {}", context, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Récupérer toutes les lignes de commande
     */
    @GetMapping
    @Operation(
            summary = "Récupérer toutes les lignes de commande",
            description = "Retourne la liste complète de toutes les lignes de commande"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = LigneCommandeDto.class))
            )
    )
    public ResponseEntity<List<LigneCommandeDto>> getAllLigneCommandes(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les lignes de commande", context);
        List<LigneCommandeDto> ligneCommandes = ligneCommandeService.getAll();
        logger.info("{} {} lignes de commande récupérées avec succès", context, ligneCommandes.size());
        return ResponseEntity.ok(ligneCommandes);
    }

    /**
     * Récupérer une ligne de commande par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer une ligne de commande par ID",
            description = "Retourne les détails d'une ligne de commande spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ligne de commande trouvée"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ligne de commande non trouvée"
            )
    })
    public ResponseEntity<?> getLigneCommandeById(
            @Parameter(description = "ID de la ligne de commande", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la ligne de commande avec l'ID: {}", context, id);
        try {
            LigneCommandeDto ligneCommande = ligneCommandeService.getById(id);
            logger.info("{} Ligne de commande ID: {} récupérée avec succès", context, id);
            return ResponseEntity.ok(ligneCommande);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération de la ligne de commande ID: {} - {}", context, id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Mettre à jour une ligne de commande
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une ligne de commande",
            description = "Met à jour les informations d'une ligne de commande (quantité, notes, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ligne de commande mise à jour avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ligne de commande non trouvée"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            )
    })
    public ResponseEntity<?> updateLigneCommande(
            @Parameter(description = "ID de la ligne de commande à modifier", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations",
                    required = true
            )
            @RequestBody LigneCommandeDto ligneCommandeDto,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour de la ligne de commande ID: {}", context, id);
        try {
            LigneCommandeDto updated = ligneCommandeService.update(id, ligneCommandeDto);
            logger.info("{} Ligne de commande ID: {} mise à jour avec succès", context, id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la mise à jour de la ligne de commande ID: {} - {}", context, id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Supprimer une ligne de commande
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une ligne de commande",
            description = "Retire un article d'une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Ligne de commande supprimée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ligne de commande non trouvée"
            )
    })
    public ResponseEntity<?> deleteLigneCommande(
            @Parameter(description = "ID de la ligne de commande à supprimer", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de la ligne de commande ID: {}", context, id);
        try {
            ligneCommandeService.delete(id);
            logger.info("{} Ligne de commande ID: {} supprimée avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la suppression de la ligne de commande ID: {} - {}", context, id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Récupérer les lignes d'une commande
     */
    @GetMapping("/commande/{commandeId}")
    @Operation(
            summary = "Récupérer les lignes d'une commande",
            description = "Retourne tous les articles d'une commande spécifique"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des articles de la commande",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = LigneCommandeDto.class))
            )
    )
    public ResponseEntity<?> getLignesByCommande(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des lignes de commande pour la commande ID: {}", context, commandeId);
        try {
            List<LigneCommandeDto> lignes = ligneCommandeService.findByCommandeId(commandeId);
            logger.info("{} {} lignes de commande récupérées pour la commande ID: {}", context, lignes.size(), commandeId);
            return ResponseEntity.ok(lignes);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération des lignes de commande pour la commande ID: {} - {}", context, commandeId, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Récupérer les commandes contenant un plat
     */
    @GetMapping("/plat/{platId}")
    @Operation(
            summary = "Récupérer les lignes contenant un plat",
            description = "Retourne toutes les lignes de commande contenant un plat spécifique"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des lignes contenant ce plat"
    )
    public ResponseEntity<?> getLignesByPlat(
            @Parameter(description = "ID du plat", required = true, example = "1")
            @PathVariable Long platId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des lignes de commande pour le plat ID: {}", context, platId);
        try {
            List<LigneCommandeDto> lignes = ligneCommandeService.findByPlatId(platId);
            logger.info("{} {} lignes de commande récupérées pour le plat ID: {}", context, lignes.size(), platId);
            return ResponseEntity.ok(lignes);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la récupération des lignes de commande pour le plat ID: {} - {}", context, platId, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Ajouter un article à une commande (méthode simplifiée)
     */
    @PostMapping("/commande/{commandeId}/ajouter")
    @Operation(
            summary = "Ajouter un article à une commande",
            description = "Méthode simplifiée pour ajouter un plat à une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Article ajouté avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Plat non disponible ou quantité invalide"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande ou plat non trouvé"
            )
    })
    public ResponseEntity<?> ajouterArticle(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            @Parameter(description = "ID du plat", required = true, example = "1")
            @RequestParam Long platId,
            @Parameter(description = "Quantité", required = true, example = "2")
            @RequestParam Integer quantite,
            @Parameter(description = "Notes pour la cuisine", example = "Sans oignons")
            @RequestParam(required = false) String notes,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Ajout d'un article à la commande ID: {} - Plat ID: {}, Quantité: {}", context, commandeId, platId, quantite);
        try {
            LigneCommandeDto result = ligneCommandeService.ajouterLigneCommande(commandeId, platId, quantite, notes);
            logger.info("{} Article ajouté avec succès à la commande ID: {} - Ligne de commande ID: {}", context, commandeId, result.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de l'ajout d'un article à la commande ID: {} - {}", context, commandeId, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Modifier la quantité d'une ligne
     */
    @PatchMapping("/{id}/quantite")
    @Operation(
            summary = "Modifier la quantité d'une ligne",
            description = "Change la quantité commandée pour un article"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quantité modifiée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ligne de commande non trouvée"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Quantité invalide"
            )
    })
    public ResponseEntity<?> updateQuantite(
            @Parameter(description = "ID de la ligne de commande", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelle quantité", required = true, example = "3")
            @RequestParam Integer quantite,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Mise à jour de la quantité pour la ligne de commande ID: {} - Nouvelle quantité: {}", context, id, quantite);
        try {
            LigneCommandeDto updated = ligneCommandeService.updateQuantite(id, quantite);
            logger.info("{} Quantité mise à jour avec succès pour la ligne de commande ID: {}", context, id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la mise à jour de la quantité pour la ligne de commande ID: {} - {}", context, id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvé") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Calculer le total d'une commande
     */
    @GetMapping("/commande/{commandeId}/total")
    @Operation(
            summary = "Calculer le total d'une commande",
            description = "Retourne le montant total de tous les articles d'une commande"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Total calculé avec succès"
    )
    public ResponseEntity<?> calculateTotal(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Calcul du total pour la commande ID: {}", context, commandeId);
        try {
            BigDecimal total = ligneCommandeService.calculateTotalCommande(commandeId);
            logger.info("{} Total calculé avec succès pour la commande ID: {} - Montant: {}", context, commandeId, total);

            Map<String, Object> response = new HashMap<>();
            response.put("commandeId", commandeId);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors du calcul du total pour la commande ID: {} - {}", context, commandeId, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Supprimer tous les articles d'une commande
     */
    @DeleteMapping("/commande/{commandeId}/vider")
    @Operation(
            summary = "Vider une commande",
            description = "Supprime tous les articles d'une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Commande vidée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            )
    })
    public ResponseEntity<?> viderCommande(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de vidage de la commande ID: {}", context, commandeId);
        try {
            ligneCommandeService.supprimerToutesLignesCommande(commandeId);
            logger.info("{} Commande ID: {} vidée avec succès", context, commandeId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors du vidage de la commande ID: {} - {}", context, commandeId, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
