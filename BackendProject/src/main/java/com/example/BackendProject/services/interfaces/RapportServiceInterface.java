package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.RapportsDto;

import java.util.List;

public interface RapportServiceInterface {
    // Créer un rapport
    RapportsDto createRapport(RapportsDto rapportDto);

    // Récupérer un rapport par son ID
    RapportsDto getRapportById(Long id);

    // Récupérer tous les rapports

    List<RapportsDto> getAllRapports();

    // Mettre à jour un rapport existant
    RapportsDto updateRapport(Long id, RapportsDto rapportDto);

    // Supprimer un rapport
    void deleteRapport(Long id);
}


