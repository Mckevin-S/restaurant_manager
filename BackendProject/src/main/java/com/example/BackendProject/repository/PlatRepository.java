package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Plat;



import java.util.List;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {

    // Trouver les plats par catégorie
    List<Plat> findByCategory(Category category);

    // Rechercher des plats par nom
    List<Plat> findByNomContainingIgnoreCase(String keyword);

    @Query("SELECT p.nom, SUM(lc.quantite) as totalVendu " +
            "FROM LigneCommande lc " +
            "JOIN lc.plat p " +
            "GROUP BY p.id, p.nom " +
            "ORDER BY totalVendu DESC")
    List<Object[]> findTopSellingPlats();

    // Version limitée au Top 5
    @Query(value = "SELECT p.nom, SUM(lc.quantite) as totalVendu " +
            "FROM ligne_commande lc " +
            "JOIN plat p ON lc.plat_id = p.id " +
            "GROUP BY p.id, p.nom " +
            "ORDER BY totalVendu DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5SellingPlats();

}
