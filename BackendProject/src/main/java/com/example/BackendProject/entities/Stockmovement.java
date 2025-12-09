package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Stockmovement {
    private Long id;
    private Long ingredientId;
    private Object typeMouvement;
    private BigDecimal quantite;
    private Timestamp dateMouvement;
    private String raison;

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
    @Column(name = "ingredient_id", nullable = true)
    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    @Basic
    @Column(name = "type_mouvement", nullable = true)
    public Object getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(Object typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    @Basic
    @Column(name = "quantite", nullable = false, precision = 2)
    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    @Basic
    @Column(name = "date_mouvement", nullable = true)
    public Timestamp getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(Timestamp dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    @Basic
    @Column(name = "raison", nullable = true, length = -1)
    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stockmovement that = (Stockmovement) o;
        return Objects.equals(id, that.id) && Objects.equals(ingredientId, that.ingredientId) && Objects.equals(typeMouvement, that.typeMouvement) && Objects.equals(quantite, that.quantite) && Objects.equals(dateMouvement, that.dateMouvement) && Objects.equals(raison, that.raison);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ingredientId, typeMouvement, quantite, dateMouvement, raison);
    }
}
