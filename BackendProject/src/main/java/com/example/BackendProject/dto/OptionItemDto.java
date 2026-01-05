package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



public class OptionItemDto {

    private Long id;
    private String nom;
    private BigDecimal prixSupplementaire;

    public OptionItemDto(Long id, String nom, BigDecimal prixSupplementaire) {
        this.id = id;
        this.nom = nom;
        this.prixSupplementaire = prixSupplementaire;
    }

    public OptionItemDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getPrixSupplementaire() {
        return prixSupplementaire;
    }

    public void setPrixSupplementaire(BigDecimal prixSupplementaire) {
        this.prixSupplementaire = prixSupplementaire;
    }
}
