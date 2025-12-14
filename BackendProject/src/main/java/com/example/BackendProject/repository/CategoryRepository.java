package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
