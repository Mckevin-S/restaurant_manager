package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



public class IngredientDto {

    private Long id;
    private String nom;
    private BigDecimal quantiteActuelle;
    private String uniteMesure;
    private BigDecimal seuilAlerte;

    public IngredientDto(Long id, String nom, BigDecimal quantiteActuelle, String uniteMesure, BigDecimal seuilAlerte) {
        this.id = id;
        this.nom = nom;
        this.quantiteActuelle = quantiteActuelle;
        this.uniteMesure = uniteMesure;
        this.seuilAlerte = seuilAlerte;
    }

    public IngredientDto() {

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

    public BigDecimal getQuantiteActuelle() {
        return quantiteActuelle;
    }

    public void setQuantiteActuelle(BigDecimal quantiteActuelle) {
        this.quantiteActuelle = quantiteActuelle;
    }

    public String getUniteMesure() {
        return uniteMesure;
    }

    public void setUniteMesure(String uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    public BigDecimal getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(BigDecimal seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
}
