package com.example.BackendProject.dto;

import com.example.BackendProject.utils.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "DTO représentant un utilisateur du système")
public class UtilisateurDto {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Restaurant associé à l'utilisateur")
    private Long restaurantId;

    @Schema(description = "Rôle de l'utilisateur", example = "SERVEUR", required = true)
    @NotNull(message = "Le rôle est obligatoire")
    private RoleType role;

    @Schema(description = "Nom de famille de l'utilisateur", example = "Dupont", required = true)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @Schema(description = "Prénom de l'utilisateur", example = "Jean", required = true)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @Schema(description = "Adresse email de l'utilisateur", example = "jean.dupont@restaurant.com", required = true)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @Schema(description = "Mot de passe de l'utilisateur (non retourné dans les réponses)", example = "password123", accessMode = Schema.AccessMode.WRITE_ONLY)
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @Schema(description = "Numéro de téléphone", example = "+237123456789")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Le numéro de téléphone doit être valide")
    private String telephone;

    @Schema(description = "Date d'embauche de l'utilisateur", example = "2024-01-15")
    @PastOrPresent(message = "La date d'embauche ne peut pas être dans le futur")
    private LocalDate dateEmbauche;

    public UtilisateurDto(Long id, Long restaurantId, RoleType role, String nom, String prenom, String email, String motDePasse, String telephone, LocalDate dateEmbauche) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.dateEmbauche = dateEmbauche;
    }

    public UtilisateurDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }
}
