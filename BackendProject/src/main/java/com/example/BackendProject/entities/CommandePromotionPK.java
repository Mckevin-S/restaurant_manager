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

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandePromotionPK)) return false;
        CommandePromotionPK that = (CommandePromotionPK) o;
        return Objects.equals(commandeId, that.commandeId) &&
                Objects.equals(promotionId, that.promotionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandeId, promotionId);
    }

    // Getters & Setters
    public Long getCommandeId() { return commandeId; }
    public void setCommandeId(Long commandeId) { this.commandeId = commandeId; }
    public Long getPromotionId() { return promotionId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }
}
