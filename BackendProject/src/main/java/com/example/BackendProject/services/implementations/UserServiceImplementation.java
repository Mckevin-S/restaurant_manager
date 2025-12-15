package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.UserDto;
import com.example.BackendProject.entities.User;
import com.example.BackendProject.mappers.UserMapper;
import com.example.BackendProject.repository.UserRepository;
import com.example.BackendProject.services.interfaces.UserServiceInterface;
import com.example.BackendProject.utils.CodeGenerator;
import com.example.BackendProject.utils.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImplementation implements UserServiceInterface {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public final CodeGenerator codeGenerator;


    public UserServiceImplementation(UserMapper userMapper, UserRepository userRepository, CodeGenerator codeGenerator) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public UserDto save(UserDto userDto) {

            User user = userMapper.toEntity(userDto);

        // Chiffrement du mot de passe
        if (user.getMotDePasse() != null && !user.getMotDePasse().isBlank()) {
            user.setMotDePasse(
                    passwordEncoder.encode(user.getMotDePasse())
            );
        }
        // Génération de l'identifiant de l'utilisateur
        userDto.setId(codeGenerator.genarate(userDto.getRole().name()));

        // Sauvergarde de l'utilisateur
        User savedUser = userRepository.save(userMapper.toEntity(userDto));

        // Retourné l'utilisateur stocker sans exposer mot de passe
        UserDto resultUser = userMapper.toDto(savedUser);
        resultUser.setMotDePasse(null);

        return resultUser;

    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserDto dto = userMapper.toDto(user);
                    dto.setMotDePasse(null); // Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur avec avec le code "+ id +" est introuvable " ));

        UserDto dto = userMapper.toDto(user);
        dto.setMotDePasse(null); // Masquer le mot de passe
        return dto;
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Mise à jour des champs (sauf le mot de passe pour l'instant)
        if (userDto.getNom() != null && !userDto.getNom().isEmpty()) {
            user.setNom(userDto.getNom());
        }

        if (userDto.getPrenom() != null && !userDto.getPrenom().isEmpty()) {
            user.setPrenom(userDto.getPrenom());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getTelephone() != null && !userDto.getTelephone().isEmpty()) {
            user.setTelephone(userDto.getTelephone());
        }

        // MISE À JOUR DU MOT DE PASSE : encoder seulement si un nouveau mot de passe est fourni
        if (userDto.getMotDePasse() != null && !userDto.getMotDePasse().isEmpty()) {
            // Vérifier que ce n'est pas déjà un hash BCrypt
            if (!userDto.getMotDePasse().startsWith("$2a$") &&
                    !userDto.getMotDePasse().startsWith("$2b$")) {
                String encodedPassword = passwordEncoder.encode(userDto.getMotDePasse());
                user.setMotDePasse(encodedPassword);
            } else {
                // Si c'est déjà un hash, le garder tel quel
                user.setMotDePasse(userDto.getMotDePasse());
            }
        }
        // Sinon, on garde l'ancien mot de passe (pas de modification)

        // Mise à jour des autres champs si présents
        if (userDto.getRestaurant() != null) {
            user.setRestaurant(userDto.getRestaurant());
        }

        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }

        if (userDto.getDateEmbauche() != null) {
            user.setDateEmbauche(userDto.getDateEmbauche());
        }

        // Sauvegarde
        User updatedUser = userRepository.save(user);

        UserDto resultDto = userMapper.toDto(updatedUser);
        resultDto.setMotDePasse(null); // Ne jamais renvoyer le mot de passe

        return resultDto;
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        userRepository.delete(user);
    }

    @Override
    public List<UserDto> findByRoleType(RoleType role) {
        return userRepository.findByRole(role)
                .stream()
                .map(personnel -> {
                    UserDto dto = userMapper.toDto(personnel);
                    dto.setMotDePasse(null); //  Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> search(String keyword) {
        return userRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(personnel -> {
                    UserDto dto = userMapper.toDto(personnel);
                    dto.setMotDePasse(null); // Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(oldPassword, user.getMotDePasse())) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }

        // Validation du nouveau mot de passe (optionnel)
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setMotDePasse(encodedPassword);

        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Validation du nouveau mot de passe (optionnel)
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setMotDePasse(encodedPassword);

        userRepository.save(user);
    }




}
