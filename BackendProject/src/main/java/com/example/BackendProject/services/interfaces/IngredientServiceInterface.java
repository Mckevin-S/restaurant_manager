package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.IngredientDto;

import java.math.BigDecimal;
import java.util.List;

public interface IngredientServiceInterface {

    IngredientDto save(IngredientDto ingredientDto);

    List<IngredientDto> getAll();

    IngredientDto getById(Long id);

    IngredientDto update(Long id, IngredientDto ingredientDto);

    void delete(Long id);

    // Méthodes spécifiques
    List<IngredientDto> search(String keyword);

    List<IngredientDto> findIngredientsEnAlerte();

    List<IngredientDto> findByUniteMesure(String uniteMesure);

    IngredientDto ajouterQuantite(Long id, BigDecimal quantite);

    IngredientDto retirerQuantite(Long id, BigDecimal quantite);

    IngredientDto updateSeuilAlerte(Long id, BigDecimal nouveauSeuil);
}
