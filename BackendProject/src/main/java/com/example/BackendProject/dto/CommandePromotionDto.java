package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Promotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



public class CommandePromotionDto {

    private Commande commande;
    private Promotion promotion;

    public CommandePromotionDto(Commande commande, Promotion promotion) {
        this.commande = commande;
        this.promotion = promotion;
    }

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
