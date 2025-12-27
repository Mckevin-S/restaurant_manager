package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.OrderItemOption;

import java.util.List;

@Repository
public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption, Long> {
    // Récupérer toutes les options choisies pour une ligne de commande spécifique
    List<OrderItemOption> findByLigneCommandeId(Long ligneCommandeId);
}