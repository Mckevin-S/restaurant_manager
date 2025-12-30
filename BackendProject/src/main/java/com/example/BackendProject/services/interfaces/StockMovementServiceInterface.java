package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.StockMovementDto;
import com.example.BackendProject.utils.TypeMouvement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface StockMovementServiceInterface {

    StockMovementDto createStockMovement(StockMovementDto stockMovementDto);

    StockMovementDto updateStockMovement(Long id, StockMovementDto stockMovementDto);

    void deleteStockMovement(Long id);

    StockMovementDto getStockMovementById(Long id);

    List<StockMovementDto> getAllStockMovements();

    List<StockMovementDto> getStockMovementsByIngredientId(Long ingredientId);

    List<StockMovementDto> getStockMovementsByType(TypeMouvement typeMouvement);

    List<StockMovementDto> getStockMovementsBetweenDates(Timestamp startDate, Timestamp endDate);

    BigDecimal getTotalQuantityByIngredient(Long ingredientId);
}