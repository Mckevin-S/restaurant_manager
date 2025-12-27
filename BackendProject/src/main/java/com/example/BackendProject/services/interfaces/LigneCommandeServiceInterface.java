package com.example.BackendProject.services.interfaces;


import com.example.BackendProject.dto.LigneCommandeDto;

import java.math.BigDecimal;
import java.util.List;

public interface LigneCommandeServiceInterface {

    LigneCommandeDto save(LigneCommandeDto ligneCommandeDto);

    List<LigneCommandeDto> getAll();

    LigneCommandeDto getById(Long id);

    LigneCommandeDto update(Long id, LigneCommandeDto ligneCommandeDto);

    void delete(Long id);

    // Méthodes spécifiques
    List<LigneCommandeDto> findByCommandeId(Long commandeId);

    List<LigneCommandeDto> findByPlatId(Long platId);

    LigneCommandeDto ajouterLigneCommande(Long commandeId, Long platId, Integer quantite, String notes);

    LigneCommandeDto updateQuantite(Long id, Integer nouvelleQuantite);

    BigDecimal calculateTotalCommande(Long commandeId);

    void supprimerToutesLignesCommande(Long commandeId);
}
