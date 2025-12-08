package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ingredient", schema = "restaurant", catalog = "")
public class IngredientEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    @Basic
    @Column(name = "quantite_actuelle", nullable = true, precision = 2)
    private BigDecimal quantiteActuelle;
    @Basic
    @Column(name = "unite_mesure", nullable = true, length = 20)
    private String uniteMesure;
    @Basic
    @Column(name = "seuil_alerte", nullable = true, precision = 2)
    private BigDecimal seuilAlerte;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientEntity that = (IngredientEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(nom, that.nom) && Objects.equals(quantiteActuelle, that.quantiteActuelle) && Objects.equals(uniteMesure, that.uniteMesure) && Objects.equals(seuilAlerte, that.seuilAlerte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, quantiteActuelle, uniteMesure, seuilAlerte);
    }
}
