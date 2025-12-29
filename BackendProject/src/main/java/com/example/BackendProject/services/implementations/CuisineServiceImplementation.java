package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.utils.StatutCommande;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CuisineServiceImplementation {

    private final CommandeRepository commandeRepository;
    private final CommandeMapper commandeMapper; // Supposant que vous avez un mapper

    public CuisineServiceImplementation(CommandeRepository commandeRepository, CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
    }

    // 1. Liste des commandes pour l'écran cuisine
    public List<CommandeDto> getListeAPreparer() {
        List<StatutCommande> statutsCuisine = Arrays.asList(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION);
        return commandeRepository.findByStatutInOrderByDateHeureCommandeAsc(statutsCuisine)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    // 2. Marquer une commande comme "En cours de préparation"
    public CommandeDto commencerPreparation(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() == StatutCommande.EN_ATTENTE) {
            commande.setStatut(StatutCommande.EN_PREPARATION);
        }
        return commandeMapper.toDto(commandeRepository.save(commande));
    }

    // 3. Marquer une commande comme "Prête" (Passage en salle)
    public CommandeDto marquerCommePrete(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        commande.setStatut(StatutCommande.PRETE);
        return commandeMapper.toDto(commandeRepository.save(commande));
    }
}
