package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


public class RecetteItemDto {

    private Long id;
    private Recette recette;
    private Ingredient ingredient;
    private BigDecimal quantiteRequise;

    public RecetteItemDto(Long id, Recette recette, Ingredient ingredient, BigDecimal quantiteRequise) {
        this.id = id;
        this.recette = recette;
        this.ingredient = ingredient;
        this.quantiteRequise = quantiteRequise;
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
