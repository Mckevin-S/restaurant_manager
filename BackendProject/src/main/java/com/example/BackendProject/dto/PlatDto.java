package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Recette;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



public class PlatDto {

    private Long id;
    
    @NotNull(message = "La catégorie est obligatoire")
    private Long category;
    
    @NotBlank(message = "Le nom du plat est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le prix doit avoir au maximum 2 décimales")
    private BigDecimal prix;
    
    private String photoUrl;
    private RecetteDto recette;
    private boolean disponibilite;

    public PlatDto(Long id, Long category, String nom, String description, BigDecimal prix, String photoUrl, RecetteDto recette, boolean disponibilite) {
        this.id = id;
        this.category = category;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.photoUrl = photoUrl;
        this.recette = recette;
        this.disponibilite = disponibilite;
    }

    public PlatDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public RecetteDto getRecette() {
        return recette;
    }

    public void setRecette(RecetteDto recette) {
        this.recette = recette;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }
}