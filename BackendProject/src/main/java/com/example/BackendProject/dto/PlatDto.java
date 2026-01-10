package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Recette;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



public class PlatDto {

    private Long id;
    private Long category;
    private String nom;
    private String description;
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