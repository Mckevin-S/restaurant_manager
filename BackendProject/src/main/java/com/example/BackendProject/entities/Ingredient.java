package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Ingredient {
    private Long id;
    private String nom;
    private BigDecimal quantiteActuelle;
    private String uniteMesure;
    private BigDecimal seuilAlerte;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "nom", nullable = false, length = 100)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Basic
    @Column(name = "quantite_actuelle", nullable = true, precision = 2)
    public BigDecimal getQuantiteActuelle() {
        return quantiteActuelle;
    }

    public void setQuantiteActuelle(BigDecimal quantiteActuelle) {
        this.quantiteActuelle = quantiteActuelle;
    }

    @Basic
    @Column(name = "unite_mesure", nullable = true, length = 20)
    public String getUniteMesure() {
        return uniteMesure;
    }

    public void setUniteMesure(String uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    @Basic
    @Column(name = "seuil_alerte", nullable = true, precision = 2)
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
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) && Objects.equals(nom, that.nom) && Objects.equals(quantiteActuelle, that.quantiteActuelle) && Objects.equals(uniteMesure, that.uniteMesure) && Objects.equals(seuilAlerte, that.seuilAlerte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, quantiteActuelle, uniteMesure, seuilAlerte);
    }
}
