package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


public class RecetteItemDto {

    private Long id;
    private Long recette;
    private Long ingredient;
    private BigDecimal quantiteRequise;

    public RecetteItemDto(Long id, Long recette, Long ingredient, BigDecimal quantiteRequise) {
        this.id = id;
        this.recette = recette;
        this.ingredient = ingredient;
        this.quantiteRequise = quantiteRequise;
    }

    public RecetteItemDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecette() {
        return recette;
    }

    public void setRecette(Long recette) {
        this.recette = recette;
    }

    public Long getIngredient() {
        return ingredient;
    }

    public void setIngredient(Long ingredient) {
        this.ingredient = ingredient;
    }

    public BigDecimal getQuantiteRequise() {
        return quantiteRequise;
    }

    public void setQuantiteRequise(BigDecimal quantiteRequise) {
        this.quantiteRequise = quantiteRequise;
    }
}
