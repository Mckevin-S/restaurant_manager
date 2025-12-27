package com.example.BackendProject.services.interfaces;


import com.example.BackendProject.dto.CommandePromotionDto;

import java.util.List;

public interface CommandePromotionServiceInterface {

    CommandePromotionDto save(CommandePromotionDto commandePromotionDto);

    List<CommandePromotionDto> getAll();

    CommandePromotionDto getById(Long commandeId, Long promotionId);

    void delete(Long commandeId, Long promotionId);

    // Méthodes spécifiques
    List<CommandePromotionDto> findByCommandeId(Long commandeId);

    List<CommandePromotionDto> findByPromotionId(Long promotionId);

    CommandePromotionDto appliquerPromotion(Long commandeId, Long promotionId);

    void retirerPromotion(Long commandeId, Long promotionId);

    void retirerToutesPromotions(Long commandeId);

    boolean promotionEstAppliquee(Long commandeId, Long promotionId);
}
