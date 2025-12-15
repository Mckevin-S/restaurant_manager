package com.example.BackendProject.services.implementations;

import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.repository.UtilisateurRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UtilisateurDetailService implements UserDetailsService {

    private UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nom) throws UsernameNotFoundException {

        // 1. Cherche l'entité Personnel correspondante dans la base de données
        // L'entité Personnel est récupérée via une méthode personnalisée dans le repository.
        Utilisateur utilisateur = utilisateurRepository.findByNom(nom);


        // 2. Vérification de l'existence de l'utilisateur
        if (utilisateur == null) {
            // Si l'utilisateur n'est pas trouvé, lance une exception spécifique
            // à Spring Security qui sera capturée pour signaler l'échec d'authentification.
            throw new UsernameNotFoundException("Utilisateur not found with username: " + nom);
        }

        // 3. Construction de la liste des autorités (rôles) de l'utilisateur
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Ajoute le rôle de l'utilisateur au format attendu par Spring Security : "ROLE_NOMDUROLE".
        // Le .name() est utilisé car on suppose que le rôle est une énumération (Enum).
        authorities.add(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()));

        // 4. Retourne un objet UserDetails (ici, l'implémentation de Spring Security)
        // Cet objet est utilisé par Spring Security pour comparer le mot de passe fourni
        // par l'utilisateur avec le mot de passe stocké (getPwdPersonnel) et encoder (haché).
        return new User(
                utilisateur.getNom(), // Nom d'utilisateur (Username)
                utilisateur.getMotDePasse(),   // Mot de passe HACHÉ (Password)
                authorities                    // Liste des autorités/rôles (Authorities)
        );
    }

}
