package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Rapports;

@Repository
public interface RapportsRepository extends JpaRepository<Rapports, Long> {
}
