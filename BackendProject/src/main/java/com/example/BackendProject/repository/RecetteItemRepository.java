package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.RecetteItem;

@Repository
public interface RecetteItemRepository extends JpaRepository<RecetteItem, Long> {
}
