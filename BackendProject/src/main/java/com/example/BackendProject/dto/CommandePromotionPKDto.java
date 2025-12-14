package com.example.BackendProject.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class CommandePromotionPKDto {

    private Long commandeId;
    private Long promotionId;

    public CommandePromotionPKDto(Long commandeId, Long promotionId) {
        this.commandeId = commandeId;
        this.promotionId = promotionId;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }
}
