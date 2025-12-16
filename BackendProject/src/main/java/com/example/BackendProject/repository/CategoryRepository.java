package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Trouver les catégories par menu
    List<Category> findByMenuId(Long menuId);

    // Trouver par menu et ordonner par ordre d'affichage
    List<Category> findByMenuIdOrderByOrdreAffichageAsc(Long menuId);

    // Rechercher par nom
    List<Category> findByNomContainingIgnoreCase(String nom);

    // Obtenir l'ordre d'affichage maximum pour un menu
    @Query("SELECT MAX(c.ordreAffichage) FROM Category c WHERE c.menu.id = :menuId")
    Integer findMaxOrdreAffichageByMenuId(@Param("menuId") Long menuId);
}
