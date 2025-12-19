package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // Rechercher par nom
    List<Ingredient> findByNomContainingIgnoreCase(String nom);

    // Trouver les ingrédients en alerte (quantité actuelle <= seuil d'alerte)
    @Query("SELECT i FROM Ingredient i WHERE i.quantiteActuelle <= i.seuilAlerte")
    List<Ingredient> findIngredientsEnAlerte();

    // Trouver par unité de mesure
    List<Ingredient> findByUniteMesure(String uniteMesure);

    // Vérifier si un ingrédient avec ce nom existe déjà
    boolean existsByNomIgnoreCase(String nom);

    // Trouver les ingrédients avec quantité inférieure à un seuil donné
    @Query("SELECT i FROM Ingredient i WHERE i.quantiteActuelle < :seuil")
    List<Ingredient> findByQuantiteInferieurA(@Param("seuil") BigDecimal seuil);
}
