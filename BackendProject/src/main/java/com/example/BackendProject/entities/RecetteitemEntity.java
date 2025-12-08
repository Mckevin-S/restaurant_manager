package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "recetteitem", schema = "restaurant", catalog = "")
public class RecetteitemEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "recette_id", nullable = true)
    private Long recetteId;
    @Basic
    @Column(name = "ingredient_id", nullable = true)
    private Long ingredientId;
    @Basic
    @Column(name = "quantite_requise", nullable = true, precision = 2)
    private BigDecimal quantiteRequise;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecetteId() {
        return recetteId;
    }

    public void setRecetteId(Long recetteId) {
        this.recetteId = recetteId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

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
        RecetteitemEntity that = (RecetteitemEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(recetteId, that.recetteId) && Objects.equals(ingredientId, that.ingredientId) && Objects.equals(quantiteRequise, that.quantiteRequise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recetteId, ingredientId, quantiteRequise);
    }
}
