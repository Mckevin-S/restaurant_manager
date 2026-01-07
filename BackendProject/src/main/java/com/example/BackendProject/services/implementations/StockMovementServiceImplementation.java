package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.StockMovementDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.StockMovement;
import com.example.BackendProject.mappers.StockMovementMapper;
import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.repository.StockMovementRepository;
import com.example.BackendProject.services.interfaces.StockMovementServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.TypeMouvement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockMovementServiceImplementation implements StockMovementServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(StockMovementServiceImplementation.class);
    private final StockMovementMapper stockMovementMapper;
    private final StockMovementRepository stockMovementRepository;
    private final IngredientRepository ingredientRepository;

    public StockMovementServiceImplementation(
            StockMovementMapper stockMovementMapper,
            StockMovementRepository stockMovementRepository,
            IngredientRepository ingredientRepository) {

        this.stockMovementMapper = stockMovementMapper;
        this.stockMovementRepository = stockMovementRepository;
        this.ingredientRepository = ingredientRepository;
    }


    @Override
    public StockMovementDto createStockMovement(StockMovementDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de création d'un mouvement de stock - Type: {}, Ingrédient ID: {}, Quantité: {}", 
                    context, dto.getTypeMouvement(), 
                    dto.getIngredient() != null ? dto.getIngredient().getId() : "N/A", 
                    dto.getQuantite());

        if (dto.getIngredient() == null || dto.getIngredient().getId() == null) {
            logger.error("{} Erreur de validation: l'ingrédient est obligatoire", context);
            throw new RuntimeException("L'ingrédient est obligatoire");
        }

        if (dto.getTypeMouvement() == null) {
            logger.error("{} Erreur de validation: le type de mouvement est obligatoire", context);
            throw new RuntimeException("Le type de mouvement est obligatoire");
        }

        if (dto.getQuantite() == null || dto.getQuantite().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("{} Erreur de validation: la quantité doit être > 0", context);
            throw new RuntimeException("La quantité doit être > 0");
        }

        Ingredient ingredient = ingredientRepository.findById(dto.getIngredient().getId())
                .orElseThrow(() -> {
                    logger.error("{} Ingrédient non trouvé avec l'ID: {}", context, dto.getIngredient().getId());
                    return new RuntimeException("Ingrédient non trouvé");
                });

        if (dto.getDateMouvement() == null) {
            dto.setDateMouvement(new Timestamp(System.currentTimeMillis()));
        }

        StockMovement movement = stockMovementMapper.toEntity(dto);
        movement.setIngredient(ingredient);

        StockMovement saved = stockMovementRepository.save(movement);
        logger.info("{} Mouvement de stock créé avec succès. ID: {}, Type: {}, Quantité: {}", 
                    context, saved.getId(), saved.getTypeMouvement(), saved.getQuantite());

        return stockMovementMapper.toDto(saved);
    }



    @Override
    public StockMovementDto updateStockMovement(Long id, StockMovementDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour du mouvement de stock ID: {}", context, id);

        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Mouvement de stock non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Mouvement non trouvé");
                });

        if (dto.getTypeMouvement() != null) {
            movement.setTypeMouvement(dto.getTypeMouvement());
        }

        if (dto.getQuantite() != null && dto.getQuantite().compareTo(BigDecimal.ZERO) > 0) {
            movement.setQuantite(dto.getQuantite());
        }

        if (dto.getDateMouvement() != null) {
            movement.setDateMouvement(dto.getDateMouvement());
        }

        if (dto.getRaison() != null) {
            movement.setRaison(dto.getRaison());
        }

        if (dto.getIngredient() != null && dto.getIngredient().getId() != null) {
            Ingredient ingredient = ingredientRepository.findById(dto.getIngredient().getId())
                    .orElseThrow(() -> {
                        logger.error("{} Ingrédient non trouvé avec l'ID: {}", context, dto.getIngredient().getId());
                        return new RuntimeException("Ingrédient non trouvé");
                    });
            movement.setIngredient(ingredient);
        }

        StockMovement updated = stockMovementRepository.save(movement);
        logger.info("{} Mouvement de stock ID: {} mis à jour avec succès", context, id);
        return stockMovementMapper.toDto(updated);
    }



    @Override
    public void deleteStockMovement(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression du mouvement de stock ID: {}", context, id);
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Mouvement de stock non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Mouvement non trouvé");
                });
        stockMovementRepository.delete(movement);
        logger.info("{} Mouvement de stock ID: {} supprimé avec succès", context, id);
    }



    @Override
    public StockMovementDto getStockMovementById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération du mouvement de stock avec l'ID: {}", context, id);
        return stockMovementRepository.findById(id)
                .map(stockMovementMapper::toDto)
                .orElseThrow(() -> {
                    logger.error("{} Mouvement de stock non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Mouvement non trouvé");
                });
    }

    @Override
    public List<StockMovementDto> getAllStockMovements() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les mouvements de stock", context);
        List<StockMovementDto> movements = stockMovementRepository.findAll()
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} mouvements de stock récupérés avec succès", context, movements.size());
        return movements;
    }

    @Override
    public List<StockMovementDto> getStockMovementsByIngredientId(Long ingredientId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des mouvements de stock pour l'ingrédient ID: {}", context, ingredientId);
        if (!ingredientRepository.existsById(ingredientId)) {
            logger.error("{} Ingrédient non trouvé avec l'ID: {}", context, ingredientId);
            throw new RuntimeException("Ingrédient non trouvé");
        }

        List<StockMovementDto> movements = stockMovementRepository.findByIngredientIdOrderByDateMouvementDesc(ingredientId)
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} mouvements récupérés pour l'ingrédient ID: {}", context, movements.size(), ingredientId);
        return movements;
    }

    @Override
    public List<StockMovementDto> getStockMovementsByType(TypeMouvement type) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des mouvements de stock par type: {}", context, type);
        List<StockMovementDto> movements = stockMovementRepository
                .findByTypeMouvementOrderByDateMouvementDesc(type)
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} mouvements de type {} récupérés avec succès", context, movements.size(), type);
        return movements;
    }

    @Override
    public List<StockMovementDto> getStockMovementsBetweenDates(Timestamp start, Timestamp end) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des mouvements de stock entre {} et {}", context, start, end);
        if (start.after(end)) {
            logger.error("{} Dates invalides: date de début postérieure à la date de fin", context);
            throw new RuntimeException("Dates invalides");
        }

        List<StockMovementDto> movements = stockMovementRepository
                .findByDateMouvementBetweenOrderByDateMouvementDesc(start, end)
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} mouvements récupérés pour la période spécifiée", context, movements.size());
        return movements;
    }


    @Override
    public BigDecimal getTotalQuantityByIngredient(Long ingredientId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Calcul du total de stock pour l'ingrédient ID: {}", context, ingredientId);
        BigDecimal total = stockMovementRepository.findByIngredientId(ingredientId)
                .stream()
                .map(m -> m.getTypeMouvement() == TypeMouvement.ENTREE
                        ? m.getQuantite()
                        : m.getQuantite().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("{} Total calculé pour l'ingrédient ID: {} - Quantité: {}", context, ingredientId, total);
        return total;
    }
}
