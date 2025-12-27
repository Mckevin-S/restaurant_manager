package com.example.BackendProject.repository;

import com.example.BackendProject.entities.CommandePromotion;
import com.example.BackendProject.entities.CommandePromotionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandePromotionRepository extends JpaRepository<CommandePromotion, CommandePromotionPK> {

    // Trouver toutes les promotions d'une commande
    @Query("SELECT cp FROM CommandePromotion cp WHERE cp.commande.id = :commandeId")
    List<CommandePromotion> findByCommandeId(@Param("commandeId") Long commandeId);

    // Trouver toutes les commandes ayant une promotion spécifique
    @Query("SELECT cp FROM CommandePromotion cp WHERE cp.promotion.id = :promotionId")
    List<CommandePromotion> findByPromotionId(@Param("promotionId") Long promotionId);

    // Vérifier si une promotion est appliquée à une commande
    @Query("SELECT COUNT(cp) > 0 FROM CommandePromotion cp WHERE cp.commande.id = :commandeId AND cp.promotion.id = :promotionId")
    boolean existsByCommandeIdAndPromotionId(@Param("commandeId") Long commandeId, @Param("promotionId") Long promotionId);

    // Supprimer toutes les promotions d'une commande
    @Query("DELETE FROM CommandePromotion cp WHERE cp.commande.id = :commandeId")
    void deleteByCommandeId(@Param("commandeId") Long commandeId);

    // Supprimer une promotion spécifique d'une commande
    @Query("DELETE FROM CommandePromotion cp WHERE cp.commande.id = :commandeId AND cp.promotion.id = :promotionId")
    void deleteByCommandeIdAndPromotionId(@Param("commandeId") Long commandeId, @Param("promotionId") Long promotionId);
}