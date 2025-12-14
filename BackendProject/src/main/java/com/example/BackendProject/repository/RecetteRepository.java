package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Recette;

@Repository
public interface RecetteRepository extends JpaRepository<Recette, Long> {
}
