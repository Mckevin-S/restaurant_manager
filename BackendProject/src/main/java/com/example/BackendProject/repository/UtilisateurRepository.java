package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.utils.RoleType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    List<Utilisateur> findByRole(RoleType role);
    List<Utilisateur> searchByNom(String nom);
    List<Utilisateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    Optional<Utilisateur> findByEmail(String email);

    Utilisateur findByNom(String nom);

    // Nouvelle méthode pour récupérer les serveurs
    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'SERVEUR'")
    List<Utilisateur> findAllServeurs();

}
