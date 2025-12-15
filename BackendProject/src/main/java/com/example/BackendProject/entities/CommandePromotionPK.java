package com.example.BackendProject.entities;

import java.io.Serializable;
import java.util.Objects;

public class CommandePromotionPK implements Serializable {

    private Long commande;
    private Long promotion;

    // Constructeur par défaut
    public CommandePromotionPK() {}

    public CommandePromotionPK(Long commande, Long promotion) {
        this.commande = commande;
        this.promotion = promotion;
    }

    // Getters et Setters
    public Long getCommande() {
        return commande;
    }

    public void setCommande(Long commande) {
        this.commande = commande;
    }

    public Long getPromotion() {
        return promotion;
    }

    public void setPromotion(Long promotion) {
        this.promotion = promotion;
    }

    // IMPORTANT : Override equals() et hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandePromotionPK that = (CommandePromotionPK) o;
        return Objects.equals(commande, that.commande) &&
                Objects.equals(promotion, that.promotion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commande, promotion);
    }
}