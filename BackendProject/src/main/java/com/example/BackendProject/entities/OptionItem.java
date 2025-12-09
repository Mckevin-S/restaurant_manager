package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "option_item", schema = "restaurant", catalog = "")
public class OptionItem {
    private Long id;
    private String nom;
    private BigDecimal prixSupplementaire;

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
    @Column(name = "prix_supplementaire", nullable = true, precision = 2)
    public BigDecimal getPrixSupplementaire() {
        return prixSupplementaire;
    }

    public void setPrixSupplementaire(BigDecimal prixSupplementaire) {
        this.prixSupplementaire = prixSupplementaire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionItem that = (OptionItem) o;
        return Objects.equals(id, that.id) && Objects.equals(nom, that.nom) && Objects.equals(prixSupplementaire, that.prixSupplementaire);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prixSupplementaire);
    }
}
