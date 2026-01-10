package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.dto.LigneCommandeDto;
import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.mappers.LigneCommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.LigneCommandeRepository;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.services.interfaces.IngredientServiceInterface;
import com.example.BackendProject.services.interfaces.LigneCommandeServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LigneCommandeServiceImplementation implements LigneCommandeServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(LigneCommandeServiceImplementation.class);
    private final LigneCommandeMapper ligneCommandeMapper;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final CommandeRepository commandeRepository;
    private final PlatRepository platRepository;
    private final CommandeMapper commandeMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final IngredientServiceInterface ingredientService;
    private RestaurantDto restaurantDto;

    public LigneCommandeServiceImplementation(LigneCommandeMapper ligneCommandeMapper,
                                              LigneCommandeRepository ligneCommandeRepository,
                                              CommandeRepository commandeRepository,
                                              PlatRepository platRepository,
                                              CommandeMapper commandeMapper,
                                              SimpMessagingTemplate messagingTemplate,
                                              IngredientServiceInterface ingredientService) {
        this.ligneCommandeMapper = ligneCommandeMapper;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.commandeRepository = commandeRepository;
        this.platRepository = platRepository;
        this.commandeMapper = commandeMapper;
        this.messagingTemplate = messagingTemplate;
        this.ingredientService = ingredientService;
    }

    @Override
    public LigneCommandeDto save(LigneCommandeDto ligneCommandeDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative d'ajout d'une ligne de commande - Commande ID: {}, Plat ID: {}, Quantité: {}", 
                    context, ligneCommandeDto.getCommande(), ligneCommandeDto.getPlat(), ligneCommandeDto.getQuantite());

        if (ligneCommandeDto.getCommande() == null) {
            logger.error("{} Erreur de validation: la commande est obligatoire", context);
            throw new RuntimeException("La commande est obligatoire");
        }

        if (ligneCommandeDto.getPlat() == null) {
            logger.error("{} Erreur de validation: le plat est obligatoire", context);
            throw new RuntimeException("Le plat est obligatoire");
        }

        if (ligneCommandeDto.getQuantite() == null || ligneCommandeDto.getQuantite() <= 0) {
            logger.error("{} Erreur de validation: quantité invalide ({})", context, ligneCommandeDto.getQuantite());
            throw new RuntimeException("La quantité doit être supérieure à 0");
        }

        Long commandeId = ligneCommandeDto.getCommande();
        Long platId = ligneCommandeDto.getPlat();

        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> {
                    logger.error("{} Commande non trouvée ID: {}", context, commandeId);
                    return new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
                });

        Plat plat = platRepository.findById(platId)
                .orElseThrow(() -> {
                    logger.error("{} Plat non trouvé ID: {}", context, platId);
                    return new RuntimeException("Plat non trouvé avec l'ID : " + platId);
                });

        if (!plat.getDisponibilite()) {
            logger.warn("{} Tentative d'ajout d'un plat non disponible: {}", context, plat.getNom());
            throw new RuntimeException("Le plat '" + plat.getNom() + "' n'est pas disponible actuellement");
        }

        if (ligneCommandeDto.getPrixUnitaire() == null) {
            ligneCommandeDto.setPrixUnitaire(plat.getPrix());
        }

        LigneCommande ligneCommande = ligneCommandeMapper.toEntity(ligneCommandeDto);
        ligneCommande.setCommande(commande);
        ligneCommande.setPlat(plat);

        LigneCommande saved = ligneCommandeRepository.save(ligneCommande);
        logger.info("{} Ligne de commande sauvegardée ID: {}", context, saved.getId());

        // DÉDUCTION STOCK
        try {
            ingredientService.deduireStockPourPlat(platId, ligneCommandeDto.getQuantite());
        } catch (Exception e) {
            logger.error("{} Erreur stock: {}", context, e.getMessage());
            // On ne bloque pas la commande, mais on log l'erreur
        }

        mettreAJourEtNotifierAddition(commandeId);

        return ligneCommandeMapper.toDto(saved);
    }

    @Override
    public List<LigneCommandeDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les lignes de commande", context);
        return ligneCommandeRepository.findAll()
                .stream()
                .map(ligneCommandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LigneCommandeDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la ligne de commande ID: {}", context, id);
        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Ligne de commande non trouvée ID: {}", context, id);
                    return new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id);
                });

        return ligneCommandeMapper.toDto(ligneCommande);
    }

    @Override
    public LigneCommandeDto update(Long id, LigneCommandeDto ligneCommandeDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Mise à jour de la ligne de commande ID: {}", context, id);

        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Ligne de commande non trouvée ID: {}", context, id);
                    return new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id);
                });

        if (ligneCommandeDto.getQuantite() != null) {
            if (ligneCommandeDto.getQuantite() <= 0) {
                throw new RuntimeException("La quantité doit être supérieure à 0");
            }
            ligneCommande.setQuantite(ligneCommandeDto.getQuantite());
        }

        if (ligneCommandeDto.getPrixUnitaire() != null) {
            if (ligneCommandeDto.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Le prix unitaire doit être supérieur à 0");
            }
            ligneCommande.setPrixUnitaire(ligneCommandeDto.getPrixUnitaire());
        }

        if (ligneCommandeDto.getNotesCuisine() != null) {
            ligneCommande.setNotesCuisine(ligneCommandeDto.getNotesCuisine());
        }

        if (ligneCommandeDto.getPlat() != null) {
            Plat plat = platRepository.findById(ligneCommandeDto.getPlat())
                    .orElseThrow(() -> new RuntimeException("Plat non trouvé avec l'ID : " + ligneCommandeDto.getPlat()));

            if (!plat.getDisponibilite()) {
                throw new RuntimeException("Le plat '" + plat.getNom() + "' n'est pas disponible actuellement");
            }
            ligneCommande.setPlat(plat);
        }

        LigneCommande updated = ligneCommandeRepository.save(ligneCommande);
        logger.info("{} Ligne de commande ID: {} mise à jour avec succès", context, id);

        mettreAJourEtNotifierAddition(updated.getCommande().getId());

        return ligneCommandeMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Suppression de la ligne de commande ID: {}", context, id);

        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Ligne de commande non trouvée ID: {}", context, id);
                    return new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id);
                });

        Long commandeId = ligneCommande.getCommande().getId();
        Long platId = ligneCommande.getPlat().getId();
        Integer quantite = ligneCommande.getQuantite();
        
        ligneCommandeRepository.delete(ligneCommande);
        logger.info("{} Ligne de commande ID: {} supprimée", context, id);

        // RESTAURATION STOCK
        try {
            ingredientService.restaurerStockPourPlat(platId, quantite);
        } catch (Exception e) {
            logger.error("{} Erreur restauration stock: {}", context, e.getMessage());
        }

        mettreAJourEtNotifierAddition(commandeId);
    }

    @Override
    public List<LigneCommandeDto> findByCommandeId(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche des lignes pour la commande ID: {}", context, commandeId);
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        return ligneCommandeRepository.findByCommandeId(commandeId)
                .stream()
                .map(ligneCommandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneCommandeDto> findByPlatId(Long platId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche des lignes pour le plat ID: {}", context, platId);
        if (!platRepository.existsById(platId)) {
            throw new RuntimeException("Plat non trouvé avec l'ID : " + platId);
        }

        return ligneCommandeRepository.findByPlatId(platId)
                .stream()
                .map(ligneCommandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LigneCommandeDto ajouterLigneCommande(Long commandeId, Long platId, Integer quantite, String notes) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Ajout direct - Commande: {}, Plat: {}, Qté: {}", context, commandeId, platId, quantite);
        
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + commandeId));

        Plat plat = platRepository.findById(platId)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé avec l'ID : " + platId));

        if (!plat.getDisponibilite()) {
            throw new RuntimeException("Le plat '" + plat.getNom() + "' n'est pas disponible actuellement");
        }

        if (quantite == null || quantite <= 0) {
            throw new RuntimeException("La quantité doit être supérieure à 0");
        }

        LigneCommande ligneCommande = new LigneCommande();
        ligneCommande.setCommande(commande);
        ligneCommande.setPlat(plat);
        ligneCommande.setQuantite(quantite);
        ligneCommande.setPrixUnitaire(plat.getPrix());
        ligneCommande.setNotesCuisine(notes);

        LigneCommande saved = ligneCommandeRepository.save(ligneCommande);
        
        // DÉDUCTION STOCK
        try {
            ingredientService.deduireStockPourPlat(platId, quantite);
        } catch (Exception e) {
            logger.error("{} Erreur stock: {}", context, e.getMessage());
        }

        mettreAJourEtNotifierAddition(commandeId);
        
        return ligneCommandeMapper.toDto(saved);
    }

    @Override
    public LigneCommandeDto updateQuantite(Long id, Integer nouvelleQuantite) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Mise à jour quantité ligne ID: {} -> {}", context, id, nouvelleQuantite);
        
        if (nouvelleQuantite == null || nouvelleQuantite <= 0) {
            throw new RuntimeException("La quantité doit être supérieure à 0");
        }

        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id));

        Integer ancienneQuantite = ligneCommande.getQuantite();
        ligneCommande.setQuantite(nouvelleQuantite);
        LigneCommande updated = ligneCommandeRepository.save(ligneCommande);

        // AJUSTEMENT STOCK
        try {
            if (nouvelleQuantite > ancienneQuantite) {
                ingredientService.deduireStockPourPlat(ligneCommande.getPlat().getId(), nouvelleQuantite - ancienneQuantite);
            } else if (nouvelleQuantite < ancienneQuantite) {
                ingredientService.restaurerStockPourPlat(ligneCommande.getPlat().getId(), ancienneQuantite - nouvelleQuantite);
            }
        } catch (Exception e) {
            logger.error("{} Erreur ajustement stock: {}", context, e.getMessage());
        }

        mettreAJourEtNotifierAddition(updated.getCommande().getId());

        return ligneCommandeMapper.toDto(updated);
    }

    @Override
    public BigDecimal calculateTotalCommande(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Calcul total pour commande ID: {}", context, commandeId);
        
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        BigDecimal total = ligneCommandeRepository.calculateTotalCommande(commandeId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public void supprimerToutesLignesCommande(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Suppression de toutes les lignes pour la commande ID: {}", context, commandeId);
        
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        // RESTAURATION STOCK POUR CHAQUE LIGNE
        try {
            List<LigneCommande> lignes = ligneCommandeRepository.findByCommandeId(commandeId);
            for (LigneCommande ligne : lignes) {
                ingredientService.restaurerStockPourPlat(ligne.getPlat().getId(), ligne.getQuantite());
            }
        } catch (Exception e) {
            logger.error("{} Erreur restauration stock massive: {}", context, e.getMessage());
        }

        ligneCommandeRepository.deleteByCommandeId(commandeId);
        mettreAJourEtNotifierAddition(commandeId);
    }

    private void mettreAJourEtNotifierAddition(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recalcul des totaux et notification pour la commande ID: {}", context, commandeId);
        
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        BigDecimal totalHt = commande.getLignes().stream()
                .map(l -> l.getPrixUnitaire().multiply(new BigDecimal(l.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Correction ici: On utilise 19.25 pour le calcul si restaurantDto n'est pas encore initialisé
        BigDecimal tauxTva = (restaurantDto != null) ? new BigDecimal(String.valueOf(restaurantDto.getTauxTva())) : new BigDecimal("0.1925");
        BigDecimal tva = totalHt.multiply(tauxTva);
        BigDecimal totalTtc = totalHt.add(tva);

        commande.setTotalHt(totalHt);
        commande.setTotalTtc(totalTtc);

        commandeRepository.save(commande);
        logger.info("{} Totaux mis à jour - HT: {}, TTC: {}", context, totalHt, totalTtc);

        CommandeDto commandeDto = commandeMapper.toDto(commande);
        
        // Notifier Serveur (Addition)
        messagingTemplate.convertAndSend("/topic/serveurs/addition/" + commandeId, commandeDto);
        
        // Notifier Cuisine (Mise à jour commande)
        messagingTemplate.convertAndSend("/topic/cuisine/commandes", commandeDto);
        
        logger.info("{} Notification envoyée sur /topic/serveurs/addition/{} et /topic/cuisine/commandes", context, commandeId);
    }
}