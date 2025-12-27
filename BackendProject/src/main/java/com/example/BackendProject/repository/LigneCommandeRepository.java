package com.example.BackendProject.repository;


import com.example.BackendProject.entities.LigneCommande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    // Trouver toutes les lignes d'une commande
    @Query("SELECT lc FROM LigneCommande lc WHERE lc.commande.id = :commandeId")
    List<LigneCommande> findByCommandeId(@Param("commandeId") Long commandeId);

    // Trouver toutes les lignes contenant un plat spécifique
    @Query("SELECT lc FROM LigneCommande lc WHERE lc.plat.id = :platId")
    List<LigneCommande> findByPlatId(@Param("platId") Long platId);

    // Calculer le total d'une commande
    @Query("SELECT SUM(lc.quantite * lc.prixUnitaire) FROM LigneCommande lc WHERE lc.commande.id = :commandeId")
    BigDecimal calculateTotalCommande(@Param("commandeId") Long commandeId);

    // Compter le nombre de lignes d'une commande
    @Query("SELECT COUNT(lc) FROM LigneCommande lc WHERE lc.commande.id = :commandeId")
    Long countByCommandeId(@Param("commandeId") Long commandeId);

    // Supprimer toutes les lignes d'une commande
    @Query("DELETE FROM LigneCommande lc WHERE lc.commande.id = :commandeId")
    void deleteByCommandeId(@Param("commandeId") Long commandeId);

    // Trouver les plats les plus commandés
    @Query("SELECT lc.plat, SUM(lc.quantite) as totalVendu " +
            "FROM LigneCommande lc " +
            "GROUP BY lc.plat " +
            "ORDER BY totalVendu DESC")
    List<Object[]> findTopSellingPlats(Pageable pageable);
}
