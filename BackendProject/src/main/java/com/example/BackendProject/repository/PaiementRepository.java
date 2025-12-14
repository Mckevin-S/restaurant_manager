package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Paiement;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
}
