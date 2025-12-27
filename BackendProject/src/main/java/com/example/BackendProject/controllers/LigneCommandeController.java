package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.LigneCommandeDto;
import com.example.BackendProject.services.implementations.LigneCommandeServiceImplementation;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ligne-commandes")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Lignes de Commande", description = "API pour gérer les lignes de commande (articles commandés)")
public class LigneCommandeController {

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
            @RequestBody LigneCommandeDto ligneCommandeDto) {
        try {
            LigneCommandeDto saved = ligneCommandeService.save(ligneCommandeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
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
    public ResponseEntity<List<LigneCommandeDto>> getAllLigneCommandes() {
        List<LigneCommandeDto> ligneCommandes = ligneCommandeService.getAll();
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
            @PathVariable Long id) {
        try {
            LigneCommandeDto ligneCommande = ligneCommandeService.getById(id);
            return ResponseEntity.ok(ligneCommande);
        } catch (RuntimeException e) {
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
            @RequestBody LigneCommandeDto ligneCommandeDto) {
        try {
            LigneCommandeDto updated = ligneCommandeService.update(id, ligneCommandeDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
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
            @PathVariable Long id) {
        try {
            ligneCommandeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
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
            @PathVariable Long commandeId) {
        try {
            List<LigneCommandeDto> lignes = ligneCommandeService.findByCommandeId(commandeId);
            return ResponseEntity.ok(lignes);
        } catch (RuntimeException e) {
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
            @PathVariable Long platId) {
        try {
            List<LigneCommandeDto> lignes = ligneCommandeService.findByPlatId(platId);
            return ResponseEntity.ok(lignes);
        } catch (RuntimeException e) {
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
            @RequestParam(required = false) String notes) {
        try {
            LigneCommandeDto result = ligneCommandeService.ajouterLigneCommande(commandeId, platId, quantite, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
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
            @RequestParam Integer quantite) {
        try {
            LigneCommandeDto updated = ligneCommandeService.updateQuantite(id, quantite);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
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
            @PathVariable Long commandeId) {
        try {
            BigDecimal total = ligneCommandeService.calculateTotalCommande(commandeId);

            Map<String, Object> response = new HashMap<>();
            response.put("commandeId", commandeId);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
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
            @PathVariable Long commandeId) {
        try {
            ligneCommandeService.supprimerToutesLignesCommande(commandeId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
