package com.example.BackendProject.entities;

import java.io.Serializable;
import java.util.Objects;

public class CommandePromotionPK implements Serializable {

    private Long commandeId;
    private Long promotionId;

    public CommandePromotionPK() {}

    public CommandePromotionPK(Long commandeId, Long promotionId) {
        this.commandeId = commandeId;
        this.promotionId = promotionId;
    }

    // Getters & Setters
    public Long getCommandeId() { return commandeId; }
    public void setCommandeId(Long commandeId) { this.commandeId = commandeId; }
    public Long getPromotionId() { return promotionId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }
}
