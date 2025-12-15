package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.mappers.UtilisateurMapper;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.interfaces.UtilisateurServiceInterface;
import com.example.BackendProject.utils.CodeGenerator;
import com.example.BackendProject.utils.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurServiceImplementation implements UtilisateurServiceInterface {

    private final UtilisateurMapper utilisateurMapper;

    private final UtilisateurRepository utilisateurRepository;
    private PasswordEncoder passwordEncoder;
    public final CodeGenerator codeGenerator;


    public UtilisateurServiceImplementation(UtilisateurMapper utilisateurMapper, UtilisateurRepository utilisateurRepository, CodeGenerator codeGenerator) {
        this.utilisateurMapper = utilisateurMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public UtilisateurDto save(UtilisateurDto utilisateurDto) {

            Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDto);

        // Chiffrement du mot de passe
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(
                    passwordEncoder.encode(utilisateur.getMotDePasse())
            );
        }
        // Génération de l'identifiant de l'utilisateur
        utilisateurDto.setId(codeGenerator.genarate(utilisateurDto.getRole().name()));

        // Sauvergarde de l'utilisateur
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateurMapper.toEntity(utilisateurDto));

        // Retourné l'utilisateur stocker sans exposer mot de passe
        UtilisateurDto resultUser = utilisateurMapper.toDto(savedUtilisateur);
        resultUser.setMotDePasse(null);

        return resultUser;

    }

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
        if (utilisateurDto.getRestaurant() != null) {
            utilisateur.setRestaurant(utilisateurDto.getRestaurant());
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
