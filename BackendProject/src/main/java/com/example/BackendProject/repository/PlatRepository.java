package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Plat;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {
}
