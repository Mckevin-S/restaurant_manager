package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.entities.Long;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
}
