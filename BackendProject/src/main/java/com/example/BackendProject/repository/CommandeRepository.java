package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Commande;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
