package com.example.BackendProject.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "commande_promation") // correspond à ton script SQL
@IdClass(CommandePromotionPK.class)  //  AJOUTER CETTE LIGNE IMPORTANTE
public class CommandePromotion {

    @Id
    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @Id
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    public CommandePromotion(Commande commande, Promotion promotion) {
        this.commande = commande;
        this.promotion = promotion;
    }

    public CommandePromotion() {
    }

    // Getters & Setters
    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
}