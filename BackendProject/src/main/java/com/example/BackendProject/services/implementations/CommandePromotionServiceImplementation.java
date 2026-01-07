package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandePromotionDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.CommandePromotion;
import com.example.BackendProject.entities.CommandePromotionPK;
import com.example.BackendProject.entities.Promotion;
import com.example.BackendProject.mappers.CommandePromotionMapper;
import com.example.BackendProject.repository.CommandePromotionRepository;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.PromotionRepository;
import com.example.BackendProject.services.interfaces.CommandePromotionServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommandePromotionServiceImplementation implements CommandePromotionServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CommandePromotionServiceImplementation.class);
    private final CommandePromotionMapper commandePromotionMapper;
    private final CommandePromotionRepository commandePromotionRepository;
    private final CommandeRepository commandeRepository;
    private final PromotionRepository promotionRepository;

    public CommandePromotionServiceImplementation(CommandePromotionMapper commandePromotionMapper,
                                                 CommandePromotionRepository commandePromotionRepository,
                                                 CommandeRepository commandeRepository,
                                                 PromotionRepository promotionRepository) {
        this.commandePromotionMapper = commandePromotionMapper;
        this.commandePromotionRepository = commandePromotionRepository;
        this.commandeRepository = commandeRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public CommandePromotionDto save(CommandePromotionDto commandePromotionDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative d'enregistrement d'une promotion sur une commande. CommandeID: {}, PromotionID: {}", 
                    context, commandePromotionDto.getCommande(), commandePromotionDto.getPromotion());

        // Validation
        if (commandePromotionDto.getCommande() == 0 ) {
            logger.error("{} Erreur : La commande est obligatoire", context);
            throw new RuntimeException("La commande est obligatoire");
        }

        if (commandePromotionDto.getPromotion() == 0) {
            logger.error("{} Erreur : La promotion est obligatoire", context);
            throw new RuntimeException("La promotion est obligatoire");
        }

        Long commandeId = commandePromotionDto.getCommande();
        Long promotionId = commandePromotionDto.getPromotion();

        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> {
                    logger.error("{} Commande non trouvée avec l'ID : {}", context, commandeId);
                    return new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
                });

        // Vérifier que la promotion existe
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> {
                    logger.error("{} Promotion non trouvée avec l'ID : {}", context, promotionId);
                    return new RuntimeException("Promotion non trouvée avec l'ID : " + promotionId);
                });

        // Vérifier que la promotion est active
        if (!promotion.getActif()) {
            logger.warn("{} La promotion ID: {} n'est pas active", context, promotionId);
            throw new RuntimeException("Cette promotion n'est pas active");
        }

        // Vérifier que la promotion n'est pas expirée
        if (promotion.getDateExpiration() != null) {
            java.util.Date dateExpiration = promotion.getDateExpiration();
            java.util.Date maintenant = new java.util.Date();

            if (dateExpiration.before(maintenant)) {
                logger.warn("{} La promotion ID: {} a expiré le {}", context, promotionId, dateExpiration);
                throw new RuntimeException("Cette promotion a expiré");
            }
        }

        // Vérifier que cette promotion n'est pas déjà appliquée à cette commande
        if (commandePromotionRepository.existsByCommandeIdAndPromotionId(commandeId, promotionId)) {
            logger.warn("{} Doublon : La promotion {} est déjà appliquée à la commande {}", context, promotionId, commandeId);
            throw new RuntimeException("Cette promotion est déjà appliquée à cette commande");
        }

        // Créer l'association
        CommandePromotion commandePromotion = new CommandePromotion();
        commandePromotion.setCommande(commande);
        commandePromotion.setPromotion(promotion);

        CommandePromotion saved = commandePromotionRepository.save(commandePromotion);
        logger.info("{} Promotion appliquée avec succès. ID Commande: {}, ID Promotion: {}", context, commandeId, promotionId);

        return commandePromotionMapper.toDto(saved);
    }

    @Override
    public List<CommandePromotionDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les promotions de commandes", context);
        return commandePromotionRepository.findAll()
                .stream()
                .map(commandePromotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandePromotionDto getById(Long commandeId, Long promotionId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche de l'association Commande: {} / Promotion: {}", context, commandeId, promotionId);
        
        CommandePromotionPK id = new CommandePromotionPK(commandeId, promotionId);

        CommandePromotion commandePromotion = commandePromotionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Association non trouvée pour Commande: {} et Promotion: {}", context, commandeId, promotionId);
                    return new RuntimeException("Association non trouvée");
                });

        return commandePromotionMapper.toDto(commandePromotion);
    }

    @Override
    public void delete(Long commandeId, Long promotionId) {
        String context = LoggingUtils.getLogContext();
        logger.warn("{} Tentative de suppression de la promotion {} sur la commande {}", context, promotionId, commandeId);
        
        CommandePromotionPK id = new CommandePromotionPK(commandeId, promotionId);

        if (!commandePromotionRepository.existsById(id)) {
            logger.error("{} Échec suppression : Association non trouvée", context);
            throw new RuntimeException("Association non trouvée");
        }

        commandePromotionRepository.deleteById(id);
        logger.info("{} Promotion {} retirée avec succès de la commande {}", context, promotionId, commandeId);
    }

    @Override
    public List<CommandePromotionDto> findByCommandeId(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des promotions pour la commande ID : {}", context, commandeId);
        
        if (!commandeRepository.existsById(commandeId)) {
            logger.error("{} Commande non trouvée : {}", context, commandeId);
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        return commandePromotionRepository.findByCommandeId(commandeId)
                .stream()
                .map(commandePromotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandePromotionDto> findByPromotionId(Long promotionId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des commandes utilisant la promotion ID : {}", context, promotionId);
        
        if (!promotionRepository.existsById(promotionId)) {
            logger.error("{} Promotion non trouvée : {}", context, promotionId);
            throw new RuntimeException("Promotion non trouvée avec l'ID : " + promotionId);
        }

        return commandePromotionRepository.findByPromotionId(promotionId)
                .stream()
                .map(commandePromotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandePromotionDto appliquerPromotion(Long commandeId, Long promotionId) {
        logger.info("{} Appel à appliquerPromotion (Commande: {}, Promotion: {})", LoggingUtils.getLogContext(), commandeId, promotionId);
        CommandePromotionDto dto = new CommandePromotionDto();
        dto.setCommande(commandeId);
        dto.setPromotion(promotionId);
        return save(dto);
    }

    @Override
    public void retirerPromotion(Long commandeId, Long promotionId) {
        delete(commandeId, promotionId);
    }

    @Override
    public void retirerToutesPromotions(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.warn("{} Retrait de TOUTES les promotions pour la commande ID : {}", context, commandeId);
        
        if (!commandeRepository.existsById(commandeId)) {
            logger.error("{} Commande non trouvée : {}", context, commandeId);
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        commandePromotionRepository.deleteByCommandeId(commandeId);
        logger.info("{} Toutes les promotions ont été supprimées pour la commande {}", context, commandeId);
    }

    @Override
    public boolean promotionEstAppliquee(Long commandeId, Long promotionId) {
        boolean exist = commandePromotionRepository.existsByCommandeIdAndPromotionId(commandeId, promotionId);
        logger.info("{} Vérification promotion : Commande {} a promotion {} ? {}", 
                    LoggingUtils.getLogContext(), commandeId, promotionId, exist);
        return exist;
    }
}