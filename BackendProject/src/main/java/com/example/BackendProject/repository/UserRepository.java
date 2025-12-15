package com.example.BackendProject.repository;

import com.example.BackendProject.utils.RoleType;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(RoleType role);
    List<User> searchByNom(String nom);
    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
