package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.StockMovementDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.StockMovement;
import com.example.BackendProject.mappers.StockMovementMapper;
import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.repository.StockMovementRepository;
import com.example.BackendProject.services.interfaces.StockMovementServiceInterface;
import com.example.BackendProject.utils.TypeMouvement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockMovementServiceImplementation implements StockMovementServiceInterface {

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

        if (dto.getIngredient() == null || dto.getIngredient().getId() == null) {
            throw new RuntimeException("L'ingrédient est obligatoire");
        }

        if (dto.getTypeMouvement() == null) {
            throw new RuntimeException("Le type de mouvement est obligatoire");
        }

        if (dto.getQuantite() == null || dto.getQuantite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("La quantité doit être > 0");
        }

        Ingredient ingredient = ingredientRepository.findById(dto.getIngredient().getId())
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));

        if (dto.getDateMouvement() == null) {
            dto.setDateMouvement(new Timestamp(System.currentTimeMillis()));
        }

        StockMovement movement = stockMovementMapper.toEntity(dto);
        movement.setIngredient(ingredient);

        StockMovement saved = stockMovementRepository.save(movement);

        return stockMovementMapper.toDto(saved);
    }



    @Override
    public StockMovementDto updateStockMovement(Long id, StockMovementDto dto) {

        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));

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
                    .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));
            movement.setIngredient(ingredient);
        }

        return stockMovementMapper.toDto(stockMovementRepository.save(movement));
    }



    @Override
    public void deleteStockMovement(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));
        stockMovementRepository.delete(movement);
    }



    @Override
    public StockMovementDto getStockMovementById(Long id) {
        return stockMovementRepository.findById(id)
                .map(stockMovementMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));
    }

    @Override
    public List<StockMovementDto> getAllStockMovements() {
        return stockMovementRepository.findAll()
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDto> getStockMovementsByIngredientId(Long ingredientId) {
        if (!ingredientRepository.existsById(ingredientId)) {
            throw new RuntimeException("Ingrédient non trouvé");
        }

        return stockMovementRepository.findByIngredientIdOrderByDateMouvementDesc(ingredientId)
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDto> getStockMovementsByType(TypeMouvement type) {
        return stockMovementRepository
                .findByTypeMouvementOrderByDateMouvementDesc(type)
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDto> getStockMovementsBetweenDates(Timestamp start, Timestamp end) {
        if (start.after(end)) {
            throw new RuntimeException("Dates invalides");
        }

        return stockMovementRepository
                .findByDateMouvementBetweenOrderByDateMouvementDesc(start, end)
                .stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public BigDecimal getTotalQuantityByIngredient(Long ingredientId) {

        return stockMovementRepository.findByIngredientId(ingredientId)
                .stream()
                .map(m -> m.getTypeMouvement() == TypeMouvement.ENTREE
                        ? m.getQuantite()
                        : m.getQuantite().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
