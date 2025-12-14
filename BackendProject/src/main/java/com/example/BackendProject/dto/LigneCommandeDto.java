package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


public class LigneCommandeDto {

    private Long id;
    private Commande commande;
    private Plat plat;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private String notesCuisine;

    public LigneCommandeDto(Long id, Commande commande, Plat plat, Integer quantite, BigDecimal prixUnitaire, String notesCuisine) {
        this.id = id;
        this.commande = commande;
        this.plat = plat;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.notesCuisine = notesCuisine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Plat getPlat() {
        return plat;
    }

    public void setPlat(Plat plat) {
        this.plat = plat;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getNotesCuisine() {
        return notesCuisine;
    }

    public void setNotesCuisine(String notesCuisine) {
        this.notesCuisine = notesCuisine;
    }
}
