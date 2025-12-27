package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.OptionItem;
import java.util.List;

@Repository
public interface OptionItemRepository extends JpaRepository<OptionItem, Long> {
    // Optionnel : trouver des options par nom
    List<OptionItem> findByNomContainingIgnoreCase(String nom);
}
