package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.TableRestaurantRepository;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.interfaces.CommandeServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.RoleType;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommandeServiceImplementation implements CommandeServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CommandeServiceImplementation.class);
    private final CommandeMapper commandeMapper;
    private final CommandeRepository commandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TableRestaurantRepository tableRestaurantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CommandeServiceImplementation(CommandeMapper commandeMapper,
                                         CommandeRepository commandeRepository,
                                         UtilisateurRepository UtilisateurRepository,
                                         TableRestaurantRepository tableRestaurantRepository,
                                         SimpMessagingTemplate messagingTemplate) {
        this.commandeMapper = commandeMapper;
        this.commandeRepository = commandeRepository;
        this.utilisateurRepository = UtilisateurRepository;
        this.tableRestaurantRepository = tableRestaurantRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public CommandeDto save(CommandeDto commandeDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de création d'une commande - Type: {}, Table ID: {}", 
                    context, commandeDto.getTypeCommande(), 
                    (commandeDto.getTableId() != null ? commandeDto.getTableId() : "N/A"));

        // Validation des champs obligatoires
        if (commandeDto.getTypeCommande() == null) {
            logger.error("{} Erreur de validation: le type de commande est obligatoire", context);
            throw new RuntimeException("Le type de commande est obligatoire");
        }

        // Vérifier que le serveur existe et a le bon rôle
        if (commandeDto.getServeurId() != null && commandeDto.getServeurId() != null) {
            Utilisateur serveur = utilisateurRepository.findById(commandeDto.getServeurId())
                    .orElseThrow(() -> {
                        logger.error("{} Serveur non trouvé avec l'ID: {}", context, commandeDto.getServeurId());
                        return new RuntimeException("Utilisateur non trouvé avec l'ID : " + commandeDto.getServeurId());
                    });

            if (serveur.getRole() != RoleType.SERVEUR) {
                logger.error("{} L'utilisateur ID: {} n'a pas le rôle SERVEUR", context, serveur.getId());
                throw new RuntimeException("L'utilisateur doit avoir le rôle SERVEUR");
            }
        }

        // Vérifier la table
        if (commandeDto.getTypeCommande() == TypeCommande.SUR_PLACE &&
                commandeDto.getTableId() != null &&
                commandeDto.getTableId() != null) {
            if (!tableRestaurantRepository.existsById(commandeDto.getTableId())) {
                logger.error("{} Table non trouvée ID: {}", context, commandeDto.getTableId());
                throw new RuntimeException("Table non trouvée avec l'ID : " + commandeDto.getTableId());
            }
        }

        // Valeurs par défaut
        if (commandeDto.getDateHeureCommande() == null) commandeDto.setDateHeureCommande(Timestamp.valueOf(LocalDateTime.now()));
        if (commandeDto.getStatut() == null) commandeDto.setStatut(StatutCommande.EN_ATTENTE);
        if (commandeDto.getTotalHt() == null) commandeDto.setTotalHt(BigDecimal.ZERO);
        if (commandeDto.getTotalTtc() == null) commandeDto.setTotalTtc(BigDecimal.ZERO);

        Commande commande = commandeMapper.toEntity(commandeDto);
        Commande savedCommande = commandeRepository.save(commande);

        logger.info("{} Commande enregistrée avec succès. ID: {}, Statut: {}", context, savedCommande.getId(), savedCommande.getStatut());
        
        CommandeDto resultDto = commandeMapper.toDto(savedCommande);
        
        // Notification WebSocket pour la cuisine
        if (savedCommande.getStatut() == StatutCommande.EN_ATTENTE) {
            messagingTemplate.convertAndSend("/topic/cuisine/commandes", resultDto);
            logger.info("{} Notification envoyée à la cuisine pour la nouvelle commande ID: {}", context, savedCommande.getId());
        }

        return resultDto;
    }

    @Override
    public CommandeDto update(Long id, CommandeDto commandeDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Mise à jour de la commande ID: {}", context, id);

        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Commande non trouvée pour mise à jour ID: {}", context, id);
                    return new RuntimeException("Commande non trouvée avec l'ID : " + id);
                });

        if (commandeDto.getStatut() != null) {
            logger.info("{} Changement de statut demandé: {} -> {}", context, commande.getStatut(), commandeDto.getStatut());
            commande.setStatut(commandeDto.getStatut());
        }

        // ... autres mises à jour (table, serveur, totaux)
        if (commandeDto.getTotalTtc() != null) {
            commande.setTotalTtc(commandeDto.getTotalTtc());
        }

        Commande updated = commandeRepository.save(commande);
        logger.info("{} Commande ID: {} mise à jour avec succès", context, id);
        return commandeMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.warn("{} Tentative de suppression de la commande ID: {}", context, id);

        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Impossible de supprimer : Commande ID {} introuvable", context, id);
                    return new RuntimeException("Commande non trouvée avec l'ID : " + id);
                });

        if (commande.getStatut() == StatutCommande.PAYEE) {
            logger.error("{} Échec suppression : La commande ID {} est déjà payée", context, id);
            throw new RuntimeException("Impossible de supprimer une commande déjà payée");
        }

        commandeRepository.delete(commande);
        logger.info("{} Commande ID: {} supprimée avec succès", context, id);
    }

    @Override
    public CommandeDto updateStatut(Long id, StatutCommande nouveauStatut) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Mise à jour statut commande ID: {} vers {}", context, id, nouveauStatut);

        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        try {
            validateStatutTransition(commande.getStatut(), nouveauStatut);
        } catch (RuntimeException e) {
            logger.error("{} Transition de statut invalide pour la commande {}: {}", context, id, e.getMessage());
            throw e;
        }

        commande.setStatut(nouveauStatut);
        Commande updated = commandeRepository.save(commande);
        logger.info("{} Statut mis à jour avec succès pour la commande ID: {}", context, id);
        
        CommandeDto updatedDto = commandeMapper.toDto(updated);

        // Notifications WebSocket basées sur le nouveau statut
        if (nouveauStatut == StatutCommande.PRETE) {
            messagingTemplate.convertAndSend("/topic/salle/prete", updatedDto);
            logger.info("{} Notification 'PRÊTE' envoyée pour la commande ID: {}", context, id);
        } else if (nouveauStatut == StatutCommande.PAYEE) {
            messagingTemplate.convertAndSend("/topic/serveurs/addition", updatedDto);
            logger.info("{} Notification 'PAYÉE' envoyée pour la commande ID: {}", context, id);
        } else if (nouveauStatut == StatutCommande.EN_PREPARATION || nouveauStatut == StatutCommande.EN_ATTENTE) {
            // Optionnel : notifier la cuisine si une commande change entre ces deux états
            messagingTemplate.convertAndSend("/topic/cuisine/commandes", updatedDto);
        }

        return updatedDto;
    }

    @Override
    public BigDecimal calculateTotalVentes(Timestamp debut, Timestamp fin) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Calcul total des ventes entre {} et {}", context, debut, fin);
        BigDecimal total = commandeRepository.calculateTotalVentes(debut, fin, StatutCommande.PAYEE);
        return total != null ? total : BigDecimal.ZERO;
    }

    // --- Méthodes de recherche simplifiées pour la clarté ---

    @Override
    public List<CommandeDto> getAll() {
        logger.info("{} Récupération de toutes les commandes", LoggingUtils.getLogContext());
        return commandeRepository.findAll().stream().map(commandeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CommandeDto getById(Long id) {
        return commandeRepository.findById(id).map(commandeMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    @Override
    public List<CommandeDto> findByServeur(Long serveurId) {
        logger.info("{} Recherche commandes pour serveur ID: {}", LoggingUtils.getLogContext(), serveurId);
        return commandeRepository.findByServeurId(serveurId).stream().map(commandeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CommandeDto> getCommandesAujourdhui() {
        logger.info("{} Récupération des commandes du jour", LoggingUtils.getLogContext());
        return commandeRepository.findCommandesAujourdhui().stream().map(commandeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CommandeDto> findByStatut(StatutCommande statut) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche des commandes par statut : {}", context, statut);
        
        List<CommandeDto> result = commandeRepository.findByStatut(statut)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
        
        logger.info("{} {} commandes trouvées avec le statut {}", context, result.size(), statut);
        return result;
    }

    @Override
    public List<CommandeDto> findByTypeCommande(TypeCommande typeCommande) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche des commandes par type : {}", context, typeCommande);
        
        List<CommandeDto> result = commandeRepository.findByTypeCommande(typeCommande)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
                
        logger.info("{} {} commandes trouvées pour le type {}", context, result.size(), typeCommande);
        return result;
    }

    @Override
    public List<CommandeDto> findByTable(Long tableId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche des commandes pour la table ID : {}", context, tableId);
        
        List<CommandeDto> result = commandeRepository.findByTableId(tableId)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
                
        logger.info("{} {} commandes trouvées pour la table {}", context, result.size(), tableId);
        return result;
    }

    @Override
    public List<CommandeDto> findByDateRange(Timestamp debut, Timestamp fin) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche des commandes entre {} et {}", context, debut, fin);
        
        List<CommandeDto> result = commandeRepository.findByDateHeureCommandeBetween(debut, fin)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
                
        logger.info("{} {} commandes trouvées pour la période sélectionnée", context, result.size());
        return result;
    }

    @Override
    public Long countCommandesEnCours() {
        String context = LoggingUtils.getLogContext();
        Long count = commandeRepository.countCommandesEnCours();
        logger.info("{} Nombre de commandes en cours (non payées/non annulées) : {}", context, count);
        return count;
    }

    /**
     * Valider la transition entre deux statuts avec logging des échecs
     */
    private void validateStatutTransition(StatutCommande statutActuel, StatutCommande nouveauStatut) {
        String context = LoggingUtils.getLogContext();
        
        // Vérification des états terminaux
        if (statutActuel == StatutCommande.ANNULEE || statutActuel == StatutCommande.PAYEE) {
            String errorMsg = "Impossible de modifier une commande déjà " + statutActuel;
            logger.error("{} Échec de transition : {}", context, errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // Vérification du flux logique (Cuisine)
        if (statutActuel == StatutCommande.EN_ATTENTE && nouveauStatut == StatutCommande.PRETE) {
            String errorMsg = "Transition directe EN_ATTENTE -> PRETE interdite. Passage par EN_PREPARATION obligatoire.";
            logger.warn("{} Violation du flux de cuisine : {}", context, errorMsg);
            throw new RuntimeException("Une commande en attente doit d'abord passer par 'En préparation'");
        }
        
        logger.debug("{} Validation transition réussie : {} -> {}", context, statutActuel, nouveauStatut);
    }
}