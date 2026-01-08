package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Trouver les commandes par statut
    List<Commande> findByStatut(StatutCommande statut);

    // Trouver les commandes par type
    List<Commande> findByTypeCommande(TypeCommande typeCommande);

    // Trouver les commandes par serveur (User avec role SERVEUR)
    @Query("SELECT c FROM Commande c WHERE c.serveur.id = :serveurId")
    List<Commande> findByServeurId(@Param("serveurId") Long serveurId);

    // Trouver les commandes par table
    @Query("SELECT c FROM Commande c WHERE c.table.id = :tableId")
    List<Commande> findByTableId(@Param("tableId") Long tableId);

    // Trouver les commandes entre deux dates
    List<Commande> findByDateHeureCommandeBetween(Timestamp debut, Timestamp fin);

    // Trouver les commandes par serveur et statut
    @Query("SELECT c FROM Commande c WHERE c.serveur.id = :serveurId AND c.statut = :statut")
    List<Commande> findByServeurIdAndStatut(@Param("serveurId") Long serveurId,
                                            @Param("statut") StatutCommande statut);

    // Trouver les commandes du jour
    @Query("SELECT c FROM Commande c WHERE CAST(c.dateHeureCommande AS date) = CURRENT_DATE")
    List<Commande> findCommandesAujourdhui();

    // Calculer le total des ventes par période
    @Query("SELECT SUM(c.totalTtc) FROM Commande c WHERE c.dateHeureCommande BETWEEN :debut AND :fin AND c.statut = :statut")
    BigDecimal calculateTotalVentes(@Param("debut") Timestamp debut,
                                    @Param("fin") Timestamp fin,
                                    @Param("statut") StatutCommande statut);

    // Compter les commandes en cours
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.statut IN ('EN_ATTENTE', 'EN_PREPARATION')")
    Long countCommandesEnCours();

    // Récupère les commandes à traiter en cuisine (Priorité aux plus anciennes)
    List<Commande> findByStatutInOrderByDateHeureCommandeAsc(List<StatutCommande> statuts);
}