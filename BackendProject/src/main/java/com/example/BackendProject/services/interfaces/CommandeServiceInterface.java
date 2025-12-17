package com.example.BackendProject.services.interfaces;


import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface CommandeServiceInterface {

    CommandeDto save(CommandeDto commandeDto);

    List<CommandeDto> getAll();

    CommandeDto getById(Long id);

    CommandeDto update(Long id, CommandeDto commandeDto);

    void delete(Long id);

    // Méthodes spécifiques
    List<CommandeDto> findByStatut(StatutCommande statut);

    List<CommandeDto> findByTypeCommande(TypeCommande typeCommande);

    List<CommandeDto> findByServeur(Long serveurId);

    List<CommandeDto> findByTable(Long tableId);

    List<CommandeDto> findByDateRange(Timestamp debut, Timestamp fin);

    CommandeDto updateStatut(Long id, StatutCommande nouveauStatut);

    List<CommandeDto> getCommandesAujourdhui();

    BigDecimal calculateTotalVentes(Timestamp debut, Timestamp fin);

    Long countCommandesEnCours();
}
