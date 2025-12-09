package com.example.BackendProject.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "commande_promation") // correspond à ton script SQL
@IdClass(CommandePromotionPK.class)
public class CommandePromotion {

    @Id
    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @Id
    @ManyToOne
    @JoinColumn(name = "promation_id", nullable = false)
    private Promotion promotion;


    // Getters & Setters
    public Commande getOrder() { return commande; }
    public void setOrder(Commande commande) { this.commande = commande; }

    public Promotion getDiscount() { return promotion; }
    public void setDiscount(Promotion discount) { this.promotion = discount; }
}
