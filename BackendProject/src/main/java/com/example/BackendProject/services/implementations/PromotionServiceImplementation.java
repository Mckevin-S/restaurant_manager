package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.PromotionDto;
import com.example.BackendProject.entities.Promotion;
import com.example.BackendProject.mappers.PromotionMapper;
import com.example.BackendProject.repository.PromotionRepository;
import com.example.BackendProject.services.interfaces.PromotionServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImplementation implements PromotionServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(PromotionServiceImplementation.class);
    private final PromotionRepository repository;
    private final PromotionMapper mapper; // Injection de votre mapper déjà implémenté

    @Override
    public PromotionDto save(PromotionDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'une promotion", context);
        Promotion entity = mapper.toEntity(dto);
        Promotion savedPromotion = repository.save(entity);
        logger.info("{} Promotion sauvegardée avec succès. ID: {}", context, savedPromotion.getId());
        return mapper.toDto(savedPromotion);
    }

    @Override
    public List<PromotionDto> findAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les promotions", context);
        List<PromotionDto> promotions = repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} promotions récupérées avec succès", context, promotions.size());
        return promotions;
    }

    @Override
    public PromotionDto findById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la promotion avec l'ID: {}", context, id);
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                    logger.error("{} Promotion non trouvée avec l'ID: {}", context, id);
                    return new RuntimeException("Promotion introuvable avec l'ID : " + id);
                });
    }

    @Override
    public PromotionDto update(Long id, PromotionDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour de la promotion ID: {}", context, id);
        return repository.findById(id)
                .map(existingEntity -> {
                    // On transforme le DTO de mise à jour en entité
                    Promotion updatedEntity = mapper.toEntity(dto);
                    updatedEntity.setId(id); // On s'assure de garder le même ID
                    Promotion savedPromotion = repository.save(updatedEntity);
                    logger.info("{} Promotion ID: {} mise à jour avec succès", context, id);
                    return mapper.toDto(savedPromotion);
                })
                .orElseThrow(() -> {
                    logger.error("{} Promotion non trouvée avec l'ID: {}", context, id);
                    return new RuntimeException("Impossible de mettre à jour : Promotion inexistante");
                });
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de la promotion ID: {}", context, id);
        if (!repository.existsById(id)) {
            logger.error("{} Promotion non trouvée avec l'ID: {}", context, id);
            throw new RuntimeException("Suppression impossible : ID inconnu");
        }
        repository.deleteById(id);
        logger.info("{} Promotion ID: {} supprimée avec succès", context, id);
    }
}