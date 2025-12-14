package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "RecetteItem")
public class RecetteItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recette_id")
    private Recette recette;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private BigDecimal quantiteRequise;

    public RecetteItem(Long id, Recette recette, Ingredient ingredient, BigDecimal quantiteRequise) {
        this.id = id;
        this.recette = recette;
        this.ingredient = ingredient;
        this.quantiteRequise = quantiteRequise;
    }

    public RecetteItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Recette getRecette() {
        return recette;
    }

    public void setRecette(Recette recette) {
        this.recette = recette;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public BigDecimal getQuantiteRequise() {
        return quantiteRequise;
    }

    public void setQuantiteRequise(BigDecimal quantiteRequise) {
        this.quantiteRequise = quantiteRequise;
    }
}
