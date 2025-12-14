package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.OptionItem;

@Repository
public interface OptionItemRepository extends JpaRepository<OptionItem, Long> {
}
