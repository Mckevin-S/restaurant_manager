package com.example.BackendProject.services.interfaces;


import com.example.BackendProject.dto.PaiementDto;
import com.example.BackendProject.utils.TypePaiement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface PaiementServiceInterface {

    PaiementDto save(PaiementDto paiementDto);

    List<PaiementDto> getAll();

    PaiementDto getById(Long id);

    PaiementDto update(Long id, PaiementDto paiementDto);

    void delete(Long id);

    // Méthodes spécifiques
    List<PaiementDto> findByCommandeId(Long commandeId);

    List<PaiementDto> findByTypePaiement(TypePaiement typePaiement);

    List<PaiementDto> findByDateRange(Timestamp debut, Timestamp fin);

    PaiementDto findByReference(String referenceTransaction);

    PaiementDto effectuerPaiement(Long commandeId, BigDecimal montant, TypePaiement typePaiement);

    BigDecimal calculateTotalByPeriod(Timestamp debut, Timestamp fin);

    BigDecimal calculateTotalByTypeAndPeriod(TypePaiement type, Timestamp debut, Timestamp fin);

    List<PaiementDto> getPaiementsAujourdhui();

    boolean commandeEstPayee(Long commandeId);
}
