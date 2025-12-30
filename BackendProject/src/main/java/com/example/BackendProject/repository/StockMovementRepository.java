package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.StockMovement;
import com.example.BackendProject.utils.TypeMouvement;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByIngredientId(Long ingredientId);

    List<StockMovement> findByIngredientIdOrderByDateMouvementDesc(Long ingredientId);

    List<StockMovement> findByTypeMouvementOrderByDateMouvementDesc(TypeMouvement typeMouvement);

    List<StockMovement> findByDateMouvementBetweenOrderByDateMouvementDesc(Timestamp startDate, Timestamp endDate);

    List<StockMovement> findByRaisonContainingIgnoreCase(String keyword);
}