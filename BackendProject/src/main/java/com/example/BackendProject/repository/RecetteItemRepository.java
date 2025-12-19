package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.RecetteItem;

import java.util.List;

@Repository
public interface RecetteItemRepository extends JpaRepository<RecetteItem, Long> {

    List<RecetteItem> findByRecetteId(Long recetteId);

    List<RecetteItem> findByRecette(Recette recette);

    List<RecetteItem> findByIngredient(Ingredient ingredient);

}
