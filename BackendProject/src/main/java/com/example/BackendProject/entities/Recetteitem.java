package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Recetteitem {
    private Long id;
    private Long recetteId;
    private Long ingredientId;
    private BigDecimal quantiteRequise;

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
    @Column(name = "recette_id", nullable = true)
    public Long getRecetteId() {
        return recetteId;
    }

    public void setRecetteId(Long recetteId) {
        this.recetteId = recetteId;
    }

    @Basic
    @Column(name = "ingredient_id", nullable = true)
    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    @Basic
    @Column(name = "quantite_requise", nullable = true, precision = 2)
    public BigDecimal getQuantiteRequise() {
        return quantiteRequise;
    }

    public void setQuantiteRequise(BigDecimal quantiteRequise) {
        this.quantiteRequise = quantiteRequise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recetteitem that = (Recetteitem) o;
        return Objects.equals(id, that.id) && Objects.equals(recetteId, that.recetteId) && Objects.equals(ingredientId, that.ingredientId) && Objects.equals(quantiteRequise, that.quantiteRequise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recetteId, ingredientId, quantiteRequise);
    }
}
