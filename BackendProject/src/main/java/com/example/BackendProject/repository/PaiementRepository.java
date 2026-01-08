package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Paiement;
import com.example.BackendProject.utils.TypePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    // Trouver les paiements d'une commande
    @Query("SELECT p FROM Paiement p WHERE p.commande.id = :commandeId")
    List<Paiement> findByCommandeId(@Param("commandeId") Long commandeId);

    // Trouver les paiements par type
    List<Paiement> findByTypePaiement(TypePaiement typePaiement);

    // Trouver les paiements entre deux dates
    List<Paiement> findByDatePaiementBetween(Timestamp debut, Timestamp fin);

    // Trouver un paiement par référence de transaction
    Optional<Paiement> findByReferenceTransaction(String referenceTransaction);

    // Calculer le total des paiements par type sur une période
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.typePaiement = :type AND p.datePaiement BETWEEN :debut AND :fin")
    BigDecimal calculateTotalByTypeAndPeriod(@Param("type") TypePaiement type,
                                             @Param("debut") Timestamp debut,
                                             @Param("fin") Timestamp fin);

    // Calculer le total des paiements sur une période
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.datePaiement BETWEEN :debut AND :fin")
    BigDecimal calculateTotalByPeriod(@Param("debut") Timestamp debut, @Param("fin") Timestamp fin);

    // Vérifier si une commande a déjà été payée
    @Query("SELECT COUNT(p) > 0 FROM Paiement p WHERE p.commande.id = :commandeId")
    boolean existsByCommandeId(@Param("commandeId") Long commandeId);

    // Obtenir les paiements du jour
    @Query("SELECT p FROM Paiement p WHERE CAST(p.datePaiement AS date) = CURRENT_DATE")
    List<Paiement> findPaiementsAujourdhui();

    // Compter les paiements par type
    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.typePaiement = :type")
    Long countByTypePaiement(@Param("type") TypePaiement type);
}
