package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Plat;

import java.util.List;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {

    // Trouver les plats par catégorie
    List<Plat> findByCategorieId(Long categorieId);

    // Rechercher des plats par nom
    List<Plat> findByNomContainingIgnoreCase(String keyword);

}
