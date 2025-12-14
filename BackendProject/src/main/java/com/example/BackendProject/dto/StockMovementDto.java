package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.utils.TypeMouvement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class StockMovementDto {

    private Long id;
    private Ingredient ingredient;
    private TypeMouvement typeMouvement;
    private BigDecimal quantite;
    private Timestamp dateMouvement;
    private String raison;

    public StockMovementDto(Long id, Ingredient ingredient, TypeMouvement typeMouvement, BigDecimal quantite, Timestamp dateMouvement, String raison) {
        this.id = id;
        this.ingredient = ingredient;
        this.typeMouvement = typeMouvement;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.raison = raison;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public Timestamp getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(Timestamp dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }
}
