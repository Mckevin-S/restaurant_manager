package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.ChangePasswordRequest;
import com.example.BackendProject.dto.ResetPasswordRequest;
import com.example.BackendProject.dto.UserDto;
import com.example.BackendProject.services.implementations.UserServiceImplementation;
import com.example.BackendProject.utils.RoleType;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion des Utilisateurs", description = "API pour la gestion des utilisateurs du restaurant")
public class UserController {

    private UserServiceImplementation userServiceImplementation;



    /**
     * Créer un nouvel utilisateur
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = "Permet de créer un nouvel utilisateur avec encodage automatique du mot de passe"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Utilisateur créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            )
    })
    public ResponseEntity<UserDto> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de l'utilisateur à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            )
            @RequestBody UserDto userDto) {
        try {
            UserDto savedUser = userServiceImplementation.save(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Récupérer tous les utilisateurs
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste complète de tous les utilisateurs sans leurs mots de passe"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des utilisateurs récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
            )
    )
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userServiceImplementation.getAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer un utilisateur par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un utilisateur par ID",
            description = "Retourne les détails d'un utilisateur spécifique sans son mot de passe"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1")
            @PathVariable Long id) {
        try {
            UserDto user = userServiceImplementation.getById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un utilisateur",
            description = "Met à jour les informations d'un utilisateur existant. Seuls les champs fournis sont modifiés."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID de l'utilisateur à modifier", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de l'utilisateur",
                    required = true
            )
            @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userServiceImplementation.update(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un utilisateur",
            description = "Supprime définitivement un utilisateur du système"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Utilisateur supprimé avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID de l'utilisateur à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            userServiceImplementation.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Rechercher des utilisateurs par rôle
     */
    @GetMapping("/role/{roleType}")
    @Operation(
            summary = "Rechercher des utilisateurs par rôle",
            description = "Retourne tous les utilisateurs ayant un rôle spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des utilisateurs filtrée par rôle",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
                    )
            )
    })
    public ResponseEntity<List<UserDto>> getUsersByRole(
            @Parameter(
                    description = "Type de rôle à rechercher",
                    required = true,
                    example = "SERVEUR",
                    schema = @Schema(implementation = RoleType.class)
            )
            @PathVariable RoleType roleType) {
        List<UserDto> users = userServiceImplementation.findByRoleType(roleType);
        return ResponseEntity.ok(users);
    }

    /**
     * Rechercher des utilisateurs par nom ou prénom
     */
    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des utilisateurs",
            description = "Recherche des utilisateurs par nom ou prénom (insensible à la casse)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Résultats de la recherche",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
            )
    )
    public ResponseEntity<List<UserDto>> searchUsers(
            @Parameter(
                    description = "Mot-clé de recherche (nom ou prénom)",
                    required = true,
                    example = "Jean"
            )
            @RequestParam String keyword) {
        List<UserDto> users = userServiceImplementation.search(keyword);
        return ResponseEntity.ok(users);
    }

    /**
     * Changer le mot de passe (avec vérification de l'ancien)
     */
    @PutMapping("/{id}/change-password")
    @Operation(
            summary = "Changer le mot de passe",
            description = "Permet à un utilisateur de changer son mot de passe en vérifiant l'ancien"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mot de passe modifié avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"message\": \"Mot de passe modifié avec succès\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ancien mot de passe incorrect ou nouveau mot de passe invalide",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"L'ancien mot de passe est incorrect\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<Map<String, String>> changePassword(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ancien et nouveau mot de passe",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangePasswordRequest.class))
            )
            @RequestBody ChangePasswordRequest request) {
        try {
            userServiceImplementation.changePassword(id, request.getOldPassword(), request.getNewPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mot de passe modifié avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Réinitialiser le mot de passe (sans vérification)
     */
    @PutMapping("/{id}/reset-password")
    @Operation(
            summary = "Réinitialiser le mot de passe",
            description = "Permet à un administrateur de réinitialiser le mot de passe d'un utilisateur sans vérification de l'ancien"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mot de passe réinitialisé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"message\": \"Mot de passe réinitialisé avec succès\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nouveau mot de passe invalide",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Le mot de passe doit contenir au moins 6 caractères\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<Map<String, String>> resetPassword(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouveau mot de passe",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResetPasswordRequest.class))
            )
            @RequestBody ResetPasswordRequest request) {
        try {
            userServiceImplementation.resetPassword(id, request.getNewPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mot de passe réinitialisé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
