package com.example.BackendProject.dto;


public class CommandePromotionDto {

    private long commande;
    private long promotion;

    public CommandePromotionDto(long commande, long promotion) {
        this.commande = commande;
        this.promotion = promotion;
    }

    public CommandePromotionDto() {

    }

    public long getCommande() {
        return commande;
    }

    public void setCommande(long commande) {
        this.commande = commande;
    }

    public long getPromotion() {
        return promotion;
    }

    public void setPromotion(long promotion) {
        this.promotion = promotion;
    }
}
