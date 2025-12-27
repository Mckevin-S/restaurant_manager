package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.mappers.UtilisateurMapper;
import com.example.BackendProject.repository.RestaurantRepository;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.interfaces.UtilisateurServiceInterface;
import com.example.BackendProject.utils.CodeGenerator;
import com.example.BackendProject.utils.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurServiceImplementation implements UtilisateurServiceInterface {

    private final UtilisateurMapper utilisateurMapper;
    private final RestaurantRepository restaurantRepository;

    private final UtilisateurRepository utilisateurRepository;
    private PasswordEncoder passwordEncoder;
    public final CodeGenerator codeGenerator;


    public UtilisateurServiceImplementation(UtilisateurMapper utilisateurMapper, RestaurantRepository restaurantRepository, UtilisateurRepository utilisateurRepository, CodeGenerator codeGenerator, PasswordEncoder passwordEncoder) {
        this.utilisateurMapper = utilisateurMapper;
        this.restaurantRepository = restaurantRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.codeGenerator = codeGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UtilisateurDto save(UtilisateurDto utilisateurDto) {
        // 1. Mapper le DTO vers l'Entité
        Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDto);

        // 2. Chiffrer le mot de passe (Utilisation du passwordEncoder injecté)
        if (utilisateurDto.getMotDePasse() != null && !utilisateurDto.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDto.getMotDePasse()));
        }

        // 3. Associer le restaurant
        if (utilisateurDto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(utilisateurDto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant non trouvé avec l'ID : " + utilisateurDto.getRestaurantId()));
            utilisateur.setRestaurant(restaurant);
        }

        // 4. Générer l'ID technique/métier si nécessaire
        // Note: Si votre entité utilise un Long auto-généré, ignorez cette étape ou adaptez-la
        // utilisateur.setId(codeGenerator.generate(utilisateurDto.getRole().name()));

        // 5. Sauvegarder l'entité
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);

        // 6. Retourner le DTO sans le mot de passe
        UtilisateurDto result = utilisateurMapper.toDto(savedUtilisateur);
        result.setMotDePasse(null);
        return result;
    }

//    @Override
//    public UtilisateurDto update(Long id, UtilisateurDto utilisateurDto) {
//        Utilisateur utilisateur = utilisateurRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
//
//        // Mise à jour des champs basiques
//        if (utilisateurDto.getNom() != null) utilisateur.setNom(utilisateurDto.getNom());
//        if (utilisateurDto.getPrenom() != null) utilisateur.setPrenom(utilisateurDto.getPrenom());
//        if (utilisateurDto.getEmail() != null) utilisateur.setEmail(utilisateurDto.getEmail());
//        if (utilisateurDto.getTelephone() != null) utilisateur.setTelephone(utilisateurDto.getTelephone());
//        if (utilisateurDto.getRole() != null) utilisateur.setRole(utilisateurDto.getRole());
//
//        // Mise à jour sécurisée du mot de passe
//        if (utilisateurDto.getMotDePasse() != null && !utilisateurDto.getMotDePasse().isEmpty()) {
//            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDto.getMotDePasse()));
//        }
//
//        // CORRECTION : Mise à jour du restaurant
//        if (utilisateurDto.getRestaurantId() != null) {
//            Restaurant restaurant = restaurantRepository.findById(utilisateurDto.getRestaurantId())
//                    .orElseThrow(() -> new RuntimeException("Restaurant introuvable"));
//            utilisateur.setRestaurant(restaurant); // <--- LIGNE ESSENTIELLE
//        }
//
//        Utilisateur updated = utilisateurRepository.save(utilisateur);
//        UtilisateurDto result = utilisateurMapper.toDto(updated);
//        result.setMotDePasse(null);
//        return result;
//    }

    @Override
    public List<UtilisateurDto> getAll() {
        return utilisateurRepository.findAll()
                .stream()
                .map(user -> {
                    UtilisateurDto dto = utilisateurMapper.toDto(user);
                    dto.setMotDePasse(null); // Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurDto getById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur avec avec le code "+ id +" est introuvable " ));

        UtilisateurDto dto = utilisateurMapper.toDto(utilisateur);
        dto.setMotDePasse(null); // Masquer le mot de passe
        return dto;
    }

    @Override
    public UtilisateurDto update(Long id, UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Mise à jour des champs (sauf le mot de passe pour l'instant)
        if (utilisateurDto.getNom() != null && !utilisateurDto.getNom().isEmpty()) {
            utilisateur.setNom(utilisateurDto.getNom());
        }

        if (utilisateurDto.getPrenom() != null && !utilisateurDto.getPrenom().isEmpty()) {
            utilisateur.setPrenom(utilisateurDto.getPrenom());
        }

        if (utilisateurDto.getEmail() != null && !utilisateurDto.getEmail().isEmpty()) {
            utilisateur.setEmail(utilisateurDto.getEmail());
        }

        if (utilisateurDto.getTelephone() != null && !utilisateurDto.getTelephone().isEmpty()) {
            utilisateur.setTelephone(utilisateurDto.getTelephone());
        }

        // MISE À JOUR DU MOT DE PASSE : encoder seulement si un nouveau mot de passe est fourni
        if (utilisateurDto.getMotDePasse() != null && !utilisateurDto.getMotDePasse().isEmpty()) {
            // Vérifier que ce n'est pas déjà un hash BCrypt
            if (!utilisateurDto.getMotDePasse().startsWith("$2a$") &&
                    !utilisateurDto.getMotDePasse().startsWith("$2b$")) {
                String encodedPassword = passwordEncoder.encode(utilisateurDto.getMotDePasse());
                utilisateur.setMotDePasse(encodedPassword);
            } else {
                // Si c'est déjà un hash, le garder tel quel
                utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
            }
        }
        // Sinon, on garde l'ancien mot de passe (pas de modification)

        // Mise à jour des autres champs si présents
        if (utilisateurDto.getRestaurantId() != null) {
            // 1. On va chercher l'OBJET Restaurant complet en base de données via son ID
            Restaurant restaurant = restaurantRepository.findById(utilisateurDto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant introuvable avec l'ID : " + utilisateurDto.getRestaurantId()));
        }

        if (utilisateurDto.getRole() != null) {
            utilisateur.setRole(utilisateurDto.getRole());
        }

        if (utilisateurDto.getDateEmbauche() != null) {
            utilisateur.setDateEmbauche(utilisateurDto.getDateEmbauche());
        }

        // Sauvegarde
        Utilisateur updatedUtilisateur = utilisateurRepository.save(utilisateur);

        UtilisateurDto resultDto = utilisateurMapper.toDto(updatedUtilisateur);
        resultDto.setMotDePasse(null); // Ne jamais renvoyer le mot de passe

        return resultDto;
    }

    @Override
    public void delete(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        utilisateurRepository.delete(utilisateur);
    }

    @Override
    public List<UtilisateurDto> findByRoleType(RoleType role) {
        return utilisateurRepository.findByRole(role)
                .stream()
                .map(personnel -> {
                    UtilisateurDto dto = utilisateurMapper.toDto(personnel);
                    dto.setMotDePasse(null); //  Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UtilisateurDto> search(String keyword) {
        return utilisateurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(personnel -> {
                    UtilisateurDto dto = utilisateurMapper.toDto(personnel);
                    dto.setMotDePasse(null); // Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(oldPassword, utilisateur.getMotDePasse())) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }

        // Validation du nouveau mot de passe (optionnel)
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        utilisateur.setMotDePasse(encodedPassword);

        utilisateurRepository.save(utilisateur);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Validation du nouveau mot de passe (optionnel)
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        utilisateur.setMotDePasse(encodedPassword);

        utilisateurRepository.save(utilisateur);
    }




}
