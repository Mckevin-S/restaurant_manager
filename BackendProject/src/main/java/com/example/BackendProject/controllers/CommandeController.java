package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.services.implementations.CommandeServiceImplementation;
import com.example.BackendProject.services.implementations.UtilisateurServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.RoleType;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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

@RestController
@RequestMapping("/api/commandes")
@Tag(name = "Gestion des Commandes", description = "API pour la gestion des commandes du restaurant")
public class CommandeController {

    private static final Logger logger = LoggerFactory.getLogger(CommandeController.class);
    private final CommandeServiceImplementation commandeServiceImplementation;
    private final UtilisateurServiceImplementation utilisateurServiceImplementation;

    public CommandeController(CommandeServiceImplementation commandeServiceImplementation, UtilisateurServiceImplementation utilisateurServiceImplementation) {
        this.commandeServiceImplementation = commandeServiceImplementation;
        this.utilisateurServiceImplementation = utilisateurServiceImplementation;
    }

    /**
     * Créer une nouvelle commande
     */
    @PostMapping
    @Operation(
            summary = "Créer une nouvelle commande",
            description = "Permet de créer une nouvelle commande avec ses détails"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Commande créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommandeDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            )
    })
    public ResponseEntity<?> createCommande(@RequestBody CommandeDto commandeDto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'une commande pour le client: {}", context, commandeDto.getServeur());
        try {
            CommandeDto savedCommande = commandeServiceImplementation.save(commandeDto);
            logger.info("{} Commande créée avec succès. ID: {}", context, savedCommande.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCommande);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la création de la commande: {}", context, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Récupérer toutes les commandes
     */
    @GetMapping
    @Operation(
            summary = "Récupérer toutes les commandes",
            description = "Retourne la liste complète de toutes les commandes"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des commandes récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommandeDto.class))
            )
    )
    public ResponseEntity<List<CommandeDto>> getAllCommandes(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les commandes", context);
        List<CommandeDto> commandes = commandeServiceImplementation.getAll();
        logger.info("{} {} commandes récupérées", context, commandes.size());
        return ResponseEntity.ok(commandes);
    }

    /**
     * Récupérer une commande par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer une commande par ID",
            description = "Retourne les détails d'une commande spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Commande trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommandeDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            )
    })
    public ResponseEntity<?> getCommandeById(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la commande ID: {}", context, id);
        try {
            CommandeDto commande = commandeServiceImplementation.getById(id);
            logger.info("{} Commande ID: {} trouvée", context, id);
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            logger.error("{} Commande ID: {} non trouvée", context, id);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Mettre à jour une commande
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une commande",
            description = "Met à jour les informations d'une commande existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Commande mise à jour avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            )
    })
    public ResponseEntity<?> updateCommande(
            @Parameter(description = "ID de la commande à modifier", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody CommandeDto commandeDto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour de la commande ID: {}", context, id);
        try {
            CommandeDto updatedCommande = commandeServiceImplementation.update(id, commandeDto);
            logger.info("{} Commande ID: {} mise à jour avec succès", context, id);
            return ResponseEntity.ok(updatedCommande);
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la mise à jour de la commande ID: {} - {}", context, id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvée") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Supprimer une commande
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une commande",
            description = "Supprime une commande (sauf si elle est déjà payée)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Commande supprimée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Impossible de supprimer une commande payée"
            )
    })
    public ResponseEntity<?> deleteCommande(
            @Parameter(description = "ID de la commande à supprimer", required = true, example = "1")
            @PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.warn("{} Tentative de suppression de la commande ID: {}", context, id);
        try {
            commandeServiceImplementation.delete(id);
            logger.info("{} Commande ID: {} supprimée avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("{} Erreur lors de la suppression de la commande ID: {} - {}", context, id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("non trouvée") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
    }

    /**
     * Récupérer les commandes par statut
     */
    @GetMapping("/statut/{statut}")
    @Operation(
            summary = "Récupérer les commandes par statut",
            description = "Retourne toutes les commandes ayant un statut spécifique"
    )
    public ResponseEntity<List<CommandeDto>> getCommandesByStatut(
            @Parameter(description = "Statut de la commande", required = true)
            @PathVariable StatutCommande statut, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Recherche des commandes avec le statut: {}", context, statut);
        List<CommandeDto> commandes = commandeServiceImplementation.findByStatut(statut);
        logger.info("{} {} commandes trouvées avec le statut {}", context, commandes.size(), statut);
        return ResponseEntity.ok(commandes);
    }

    /**
     * Récupérer les commandes par type
     */
    @GetMapping("/type/{type}")
    @Operation(
            summary = "Récupérer les commandes par type",
            description = "Retourne toutes les commandes d'un type spécifique (SUR_PLACE ou A_EMPORTER)"
    )
    public ResponseEntity<List<CommandeDto>> getCommandesByType(
            @Parameter(description = "Type de commande", required = true)
            @PathVariable TypeCommande type, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Recherche des commandes de type: {}", context, type);
        List<CommandeDto> commandes = commandeServiceImplementation.findByTypeCommande(type);
        logger.info("{} {} commandes trouvées de type {}", context, commandes.size(), type);
        return ResponseEntity.ok(commandes);
    }

    /**
     * Récupérer les commandes d'un serveur
     */
    @GetMapping("/serveur/{serveurId}")
    @Operation(
            summary = "Récupérer les commandes d'un serveur",
            description = "Retourne toutes les commandes gérées par un serveur spécifique"
    )
    public ResponseEntity<?> getCommandesByServeur(
            @Parameter(description = "ID du serveur", required = true, example = "1")
            @PathVariable Long serveurId, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des commandes du serveur ID: {}", context, serveurId);
        try {
            List<CommandeDto> commandes = commandeServiceImplementation.findByServeur(serveurId);
            logger.info("{} {} commandes trouvées pour le serveur {}", context, commandes.size(), serveurId);
            return ResponseEntity.ok(commandes);
        } catch (RuntimeException e) {
            logger.error("{} Erreur récupération commandes serveur ID: {}: {}", context, serveurId, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Récupérer les commandes d'une table
     */
    @GetMapping("/table/{tableId}")
    @Operation(
            summary = "Récupérer les commandes d'une table",
            description = "Retourne toutes les commandes associées à une table spécifique"
    )
    public ResponseEntity<?> getCommandesByTable(
            @Parameter(description = "ID de la table", required = true, example = "1")
            @PathVariable Long tableId, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des commandes pour la table ID: {}", context, tableId);
        try {
            List<CommandeDto> commandes = commandeServiceImplementation.findByTable(tableId);
            logger.info("{} {} commandes trouvées pour la table {}", context, commandes.size(), tableId);
            return ResponseEntity.ok(commandes);
        } catch (RuntimeException e) {
            logger.error("{} Erreur récupération commandes table ID: {}: {}", context, tableId, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Mettre à jour le statut d'une commande
     */
    @PatchMapping("/{id}/statut")
    @Operation(
            summary = "Mettre à jour le statut d'une commande",
            description = "Permet de changer le statut d'une commande"
    )
    public ResponseEntity<?> updateStatut(
            @Parameter(description = "ID de la commande", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true)
            @RequestParam StatutCommande statut, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Mise à jour du statut de la commande ID: {} vers {}", context, id, statut);
        try {
            CommandeDto updatedCommande = commandeServiceImplementation.updateStatut(id, statut);
            logger.info("{} Statut de la commande ID: {} mis à jour avec succès", context, id);
            return ResponseEntity.ok(updatedCommande);
        } catch (RuntimeException e) {
            logger.error("{} Erreur mise à jour statut commande ID: {}: {}", context, id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Récupérer les commandes du jour
     */
    @GetMapping("/aujourd-hui")
    @Operation(
            summary = "Récupérer les commandes du jour",
            description = "Retourne toutes les commandes créées aujourd'hui"
    )
    public ResponseEntity<List<CommandeDto>> getCommandesAujourdhui(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des commandes du jour", context);
        List<CommandeDto> commandes = commandeServiceImplementation.getCommandesAujourdhui();
        logger.info("{} {} commandes récupérées pour aujourd'hui", context, commandes.size());
        return ResponseEntity.ok(commandes);
    }

    /**
     * Calculer le total des ventes sur une période
     */
    @GetMapping("/statistiques/ventes")
    @Operation(
            summary = "Calculer le total des ventes",
            description = "Retourne le montant total des commandes payées sur une période donnée"
    )
    public ResponseEntity<Map<String, Object>> getTotalVentes(
            @Parameter(description = "Date de début (format: yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @Parameter(description = "Date de fin (format: yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Calcul du total des ventes entre {} et {}", context, debut, fin);

        Timestamp timestampDebut = Timestamp.valueOf(debut);
        Timestamp timestampFin = Timestamp.valueOf(fin);

        BigDecimal total = commandeServiceImplementation.calculateTotalVentes(timestampDebut, timestampFin);

        Map<String, Object> response = new HashMap<>();
        response.put("debut", debut);
        response.put("fin", fin);
        response.put("totalVentes", total);

        logger.info("{} Total des ventes calculé: {}", context, total);
        return ResponseEntity.ok(response);
    }

    /**
     * Compter les commandes en cours
     */
    @GetMapping("/statistiques/en-cours")
    @Operation(
            summary = "Compter les commandes en cours",
            description = "Retourne le nombre de commandes en attente ou en préparation"
    )
    public ResponseEntity<Map<String, Long>> countCommandesEnCours(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Comptage des commandes en cours", context);
        Long count = commandeServiceImplementation.countCommandesEnCours();

        Map<String, Long> response = new HashMap<>();
        response.put("commandesEnCours", count);

        logger.info("{} Nombre de commandes en cours: {}", context, count);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer tous les serveurs
     */
    @GetMapping("/serveurs")
    @Operation(
            summary = "Récupérer tous les serveurs",
            description = "Retourne la liste de tous les utilisateurs ayant le rôle SERVEUR"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des serveurs récupérée avec succès"
    )
    public ResponseEntity<List<UtilisateurDto>> getAllServeurs(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de la liste des serveurs", context);
        List<UtilisateurDto> serveurs = utilisateurServiceImplementation.findByRoleType(RoleType.SERVEUR);
        logger.info("{} {} serveurs récupérés", context, serveurs.size());
        return ResponseEntity.ok(serveurs);
    }
}