package com.example.BackendProject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ingredient")
public class Ingredient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private BigDecimal quantiteActuelle;
    private String uniteMesure;
    private BigDecimal seuilAlerte;

    @OneToMany(mappedBy = "ingredient")
    private List<StockMovement> mouvements;

    @OneToMany(mappedBy = "ingredient")
    private List<RecetteItem> recetteItems;

    public Ingredient(Long id, String nom, BigDecimal quantiteActuelle, String uniteMesure, BigDecimal seuilAlerte, List<StockMovement> mouvements, List<RecetteItem> recetteItems) {
        this.id = id;
        this.nom = nom;
        this.quantiteActuelle = quantiteActuelle;
        this.uniteMesure = uniteMesure;
        this.seuilAlerte = seuilAlerte;
        this.mouvements = mouvements;
        this.recetteItems = recetteItems;
    }

    public Ingredient() {
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

    public List<StockMovement> getMouvements() {
        return mouvements;
    }

    public void setMouvements(List<StockMovement> mouvements) {
        this.mouvements = mouvements;
    }

    public List<RecetteItem> getRecetteItems() {
        return recetteItems;
    }

    public void setRecetteItems(List<RecetteItem> recetteItems) {
        this.recetteItems = recetteItems;
    }
}
