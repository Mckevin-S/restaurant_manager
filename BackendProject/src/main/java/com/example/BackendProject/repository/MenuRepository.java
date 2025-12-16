package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Menu;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    // Trouver un menu par son nom (utile pour les validations)
    Optional<Menu> findByNomIgnoreCase(String nom);
}
