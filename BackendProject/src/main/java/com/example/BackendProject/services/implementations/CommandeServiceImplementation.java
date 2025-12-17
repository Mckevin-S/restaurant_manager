package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.TableRestaurant;

import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.TableRestaurantRepository;

import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.interfaces.CommandeServiceInterface;
import com.example.BackendProject.utils.RoleType;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
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

    private final CommandeMapper commandeMapper;
    private final CommandeRepository commandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TableRestaurantRepository tableRestaurantRepository;

    public CommandeServiceImplementation(CommandeMapper commandeMapper,
                                         CommandeRepository commandeRepository,
                                         UtilisateurRepository UtilisateurRepository,
                                         TableRestaurantRepository tableRestaurantRepository) {
        this.commandeMapper = commandeMapper;
        this.commandeRepository = commandeRepository;
        this.utilisateurRepository = UtilisateurRepository;
        this.tableRestaurantRepository = tableRestaurantRepository;
    }

    @Override
    public CommandeDto save(CommandeDto commandeDto) {
        // Validation des champs obligatoires
        if (commandeDto.getTypeCommande() == null) {
            throw new RuntimeException("Le type de commande est obligatoire");
        }

        // Vérifier que le serveur existe et a le bon rôle
        if (commandeDto.getServeur() != null && commandeDto.getServeur().getId() != null) {
            Utilisateur serveur = utilisateurRepository.findById(commandeDto.getServeur().getId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + commandeDto.getServeur().getId()));

            // Vérifier que l'utilisateur est bien un serveur
            if (serveur.getRole() != RoleType.SERVEUR) {
                throw new RuntimeException("L'utilisateur doit avoir le rôle SERVEUR");
            }
        }

        // Vérifier que la table existe (si commande sur place)
        if (commandeDto.getTypeCommande() == TypeCommande.SUR_PLACE &&
                commandeDto.getTable() != null &&
                commandeDto.getTable().getId() != null) {
            if (!tableRestaurantRepository.existsById(commandeDto.getTable().getId())) {
                throw new RuntimeException("Table non trouvée avec l'ID : " + commandeDto.getTable().getId());
            }
        }

        // Définir les valeurs par défaut
        if (commandeDto.getDateHeureCommande() == null) {
            commandeDto.setDateHeureCommande(Timestamp.valueOf(LocalDateTime.now()));
        }

        if (commandeDto.getStatut() == null) {
            commandeDto.setStatut(StatutCommande.EN_ATTENTE);
        }

        if (commandeDto.getTotalHt() == null) {
            commandeDto.setTotalHt(BigDecimal.ZERO);
        }

        if (commandeDto.getTotalTtc() == null) {
            commandeDto.setTotalTtc(BigDecimal.ZERO);
        }

        Commande commande = commandeMapper.toEntity(commandeDto);
        Commande savedCommande = commandeRepository.save(commande);

        return commandeMapper.toDto(savedCommande);
    }

    @Override
    public List<CommandeDto> findByServeur(Long serveurId) {
        // Vérifier que l'utilisateur existe et est un serveur
        Utilisateur user = utilisateurRepository.findById(serveurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + serveurId));

        if (user.getRole() != RoleType.SERVEUR) {
            throw new RuntimeException("L'utilisateur avec l'ID " + serveurId + " n'est pas un serveur");
        }

        return commandeRepository.findByServeurId(serveurId)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandeDto> getAll() {
        return commandeRepository.findAll()
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandeDto getById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + id));

        return commandeMapper.toDto(commande);
    }

    @Override
    public CommandeDto update(Long id, CommandeDto commandeDto) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + id));

        // Mise à jour du statut
        if (commandeDto.getStatut() != null) {
            commande.setStatut(commandeDto.getStatut());
        }

        // Mise à jour du type de commande
        if (commandeDto.getTypeCommande() != null) {
            commande.setTypeCommande(commandeDto.getTypeCommande());
        }

        // Mise à jour de la table
        if (commandeDto.getTable() != null && commandeDto.getTable().getId() != null) {
            TableRestaurant table = tableRestaurantRepository.findById(commandeDto.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Table non trouvée avec l'ID : " + commandeDto.getTable().getId()));
            commande.setTable(table);
        }

        // Mise à jour du serveur
        if (commandeDto.getServeur() != null && commandeDto.getServeur().getId() != null) {
            Utilisateur serveur = utilisateurRepository.findById(commandeDto.getServeur().getId())
                    .orElseThrow(() -> new RuntimeException("Serveur non trouvé avec l'ID : " + commandeDto.getServeur().getId()));
            commande.setServeur(serveur);
        }

        // Mise à jour des totaux
        if (commandeDto.getTotalHt() != null) {
            commande.setTotalHt(commandeDto.getTotalHt());
        }

        if (commandeDto.getTotalTtc() != null) {
            commande.setTotalTtc(commandeDto.getTotalTtc());
        }

        // Sauvegarde
        Commande updatedCommande = commandeRepository.save(commande);

        return commandeMapper.toDto(updatedCommande);
    }

    @Override
    public void delete(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + id));

        // Vérifier que la commande peut être supprimée
        if (commande.getStatut() == StatutCommande.PAYEE) {
            throw new RuntimeException("Impossible de supprimer une commande déjà payée");
        }

        commandeRepository.delete(commande);
    }

    @Override
    public List<CommandeDto> findByStatut(StatutCommande statut) {
        return commandeRepository.findByStatut(statut)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandeDto> findByTypeCommande(TypeCommande typeCommande) {
        return commandeRepository.findByTypeCommande(typeCommande)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<CommandeDto> findByTable(Long tableId) {
        if (!tableRestaurantRepository.existsById(tableId)) {
            throw new RuntimeException("Table non trouvée avec l'ID : " + tableId);
        }

        return commandeRepository.findByTableId(tableId)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandeDto> findByDateRange(Timestamp debut, Timestamp fin) {
        if (debut.after(fin)) {
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        return commandeRepository.findByDateHeureCommandeBetween(debut, fin)
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandeDto updateStatut(Long id, StatutCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + id));

        // Validation des transitions de statut
        validateStatutTransition(commande.getStatut(), nouveauStatut);

        commande.setStatut(nouveauStatut);
        Commande updatedCommande = commandeRepository.save(commande);

        return commandeMapper.toDto(updatedCommande);
    }

    @Override
    public List<CommandeDto> getCommandesAujourdhui() {
        return commandeRepository.findCommandesAujourdhui()
                .stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateTotalVentes(Timestamp debut, Timestamp fin) {
        BigDecimal total = commandeRepository.calculateTotalVentes(debut, fin, StatutCommande.PAYEE);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Long countCommandesEnCours() {
        return commandeRepository.countCommandesEnCours();
    }

    /**
     * Valider la transition entre deux statuts
     */
    private void validateStatutTransition(StatutCommande statutActuel, StatutCommande nouveauStatut) {
        // Définir les transitions valides
        if (statutActuel == StatutCommande.ANNULEE || statutActuel == StatutCommande.PAYEE) {
            throw new RuntimeException("Impossible de modifier une commande " + statutActuel);
        }

        if (statutActuel == StatutCommande.EN_ATTENTE && nouveauStatut == StatutCommande.PRETE) {
            throw new RuntimeException("Une commande en attente doit d'abord passer par 'En préparation'");
        }
    }
}