package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.StatutCommande;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CuisineServiceImplementation {

    private static final Logger logger = LoggerFactory.getLogger(CuisineServiceImplementation.class);
    private final CommandeRepository commandeRepository;
    private final CommandeMapper commandeMapper;

    public CuisineServiceImplementation(CommandeRepository commandeRepository, CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
    }

    /**
     * Récupère la liste des commandes à traiter par la cuisine (En attente ou En préparation).
     */
    public List<CommandeDto> getListeAPreparer() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la liste des commandes pour l'écran cuisine", context);

        List<StatutCommande> statutsCuisine = Arrays.asList(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION);
        
        List<CommandeDto> commandes = commandeRepository.findByStatutInOrderByDateHeureCommandeAsc(statutsCuisine)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());

        logger.info("{} {} commandes envoyées à l'écran cuisine", context, commandes.size());
        return commandes;
    }

    /**
     * Change le statut de la commande à "EN_PREPARATION".
     */
    public CommandeDto commencerPreparation(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise en préparation de la commande ID: {}", context, commandeId);

        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> {
                    logger.error("{} Commande non trouvée pour commencer la préparation. ID: {}", context, commandeId);
                    return new RuntimeException("Commande non trouvée");
                });

        if (commande.getStatut() == StatutCommande.EN_ATTENTE) {
            commande.setStatut(StatutCommande.EN_PREPARATION);
            Commande saved = commandeRepository.save(commande);
            logger.info("{} Commande ID: {} est maintenant EN_PREPARATION", context, commandeId);
            return commandeMapper.toDto(saved);
        } else {
            logger.warn("{} La commande ID: {} ne peut pas passer en préparation car son statut actuel est: {}", 
                        context, commandeId, commande.getStatut());
            return commandeMapper.toDto(commande);
        }
    }

    /**
     * Change le statut de la commande à "PRETE".
     */
    public CommandeDto marquerCommePrete(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Marquage de la commande ID: {} comme PRETE", context, commandeId);

        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> {
                    logger.error("{} Commande non trouvée pour marquer comme prête. ID: {}", context, commandeId);
                    return new RuntimeException("Commande non trouvée");
                });

        commande.setStatut(StatutCommande.PRETE);
        Commande saved = commandeRepository.save(commande);
        
        logger.info("{} Commande ID: {} marquée comme PRETE et prête pour le service en salle", context, commandeId);
        return commandeMapper.toDto(saved);
    }
}