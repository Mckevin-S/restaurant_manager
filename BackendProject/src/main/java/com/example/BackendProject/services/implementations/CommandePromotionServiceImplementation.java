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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommandePromotionServiceImplementation implements CommandePromotionServiceInterface {

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
        // Validation
        if (commandePromotionDto.getCommande() == null || commandePromotionDto.getCommande().getId() == null) {
            throw new RuntimeException("La commande est obligatoire");
        }

        if (commandePromotionDto.getPromotion() == null || commandePromotionDto.getPromotion().getId() == null) {
            throw new RuntimeException("La promotion est obligatoire");
        }

        Long commandeId = commandePromotionDto.getCommande().getId();
        Long promotionId = commandePromotionDto.getPromotion().getId();

        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + commandeId));

        // Vérifier que la promotion existe
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion non trouvée avec l'ID : " + promotionId));

        // Vérifier que la promotion est active
        if (!promotion.getActif()) {
            throw new RuntimeException("Cette promotion n'est pas active");
        }

        // Vérifier que la promotion n'est pas expirée
        if (promotion.getDateExpiration() != null) {
            // ✅ Solution 1 : Si dateExpiration est de type Date (java.util.Date)
            java.util.Date dateExpiration = promotion.getDateExpiration();
            java.util.Date maintenant = new java.util.Date();

            if (dateExpiration.before(maintenant)) {
                throw new RuntimeException("Cette promotion a expiré");
            }

            // ✅ Solution 2 : Si dateExpiration est de type java.sql.Date
            // java.sql.Date dateExpiration = promotion.getDateExpiration();
            // java.sql.Date maintenant = new java.sql.Date(System.currentTimeMillis());
            // if (dateExpiration.before(maintenant)) {
            //     throw new RuntimeException("Cette promotion a expiré");
            // }

            // ✅ Solution 3 : Convertir Date en LocalDate
            // LocalDate dateExpirationLocal = promotion.getDateExpiration().toInstant()
            //         .atZone(ZoneId.systemDefault())
            //         .toLocalDate();
            // if (dateExpirationLocal.isBefore(LocalDate.now())) {
            //     throw new RuntimeException("Cette promotion a expiré le " + dateExpirationLocal);
            // }
        }

        // Vérifier que cette promotion n'est pas déjà appliquée à cette commande
        if (commandePromotionRepository.existsByCommandeIdAndPromotionId(commandeId, promotionId)) {
            throw new RuntimeException("Cette promotion est déjà appliquée à cette commande");
        }

        // Créer l'association
        CommandePromotion commandePromotion = new CommandePromotion();
        commandePromotion.setCommande(commande);
        commandePromotion.setPromotion(promotion);

        CommandePromotion saved = commandePromotionRepository.save(commandePromotion);

        return commandePromotionMapper.toDto(saved);
    }

    @Override
    public List<CommandePromotionDto> getAll() {
        return commandePromotionRepository.findAll()
                .stream()
                .map(commandePromotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandePromotionDto getById(Long commandeId, Long promotionId) {
        CommandePromotionPK id = new CommandePromotionPK(commandeId, promotionId);

        CommandePromotion commandePromotion = commandePromotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Association non trouvée pour la commande " + commandeId + " et la promotion " + promotionId
                ));

        return commandePromotionMapper.toDto(commandePromotion);
    }

    @Override
    public void delete(Long commandeId, Long promotionId) {
        CommandePromotionPK id = new CommandePromotionPK(commandeId, promotionId);

        if (!commandePromotionRepository.existsById(id)) {
            throw new RuntimeException(
                    "Association non trouvée pour la commande " + commandeId + " et la promotion " + promotionId
            );
        }

        commandePromotionRepository.deleteById(id);
    }

    @Override
    public List<CommandePromotionDto> findByCommandeId(Long commandeId) {
        // Vérifier que la commande existe
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        return commandePromotionRepository.findByCommandeId(commandeId)
                .stream()
                .map(commandePromotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandePromotionDto> findByPromotionId(Long promotionId) {
        // Vérifier que la promotion existe
        if (!promotionRepository.existsById(promotionId)) {
            throw new RuntimeException("Promotion non trouvée avec l'ID : " + promotionId);
        }

        return commandePromotionRepository.findByPromotionId(promotionId)
                .stream()
                .map(commandePromotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandePromotionDto appliquerPromotion(Long commandeId, Long promotionId) {
        // Créer un DTO pour utiliser la méthode save
        CommandePromotionDto dto = new CommandePromotionDto();

        Commande commande = new Commande();
        commande.setId(commandeId);
        dto.setCommande(commande);

        Promotion promotion = new Promotion();
        promotion.setId(promotionId);
        dto.setPromotion(promotion);

        return save(dto);
    }

    @Override
    public void retirerPromotion(Long commandeId, Long promotionId) {
        delete(commandeId, promotionId);
    }

    @Override
    public void retirerToutesPromotions(Long commandeId) {
        // Vérifier que la commande existe
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        commandePromotionRepository.deleteByCommandeId(commandeId);
    }

    @Override
    public boolean promotionEstAppliquee(Long commandeId, Long promotionId) {
        return commandePromotionRepository.existsByCommandeIdAndPromotionId(commandeId, promotionId);
    }
}
