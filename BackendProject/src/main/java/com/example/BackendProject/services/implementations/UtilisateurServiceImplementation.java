package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.mappers.UtilisateurMapper;
import com.example.BackendProject.repository.RestaurantRepository;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.interfaces.UtilisateurServiceInterface;
import com.example.BackendProject.utils.CodeGenerator;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurServiceImplementation implements UtilisateurServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurServiceImplementation.class);
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
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'un utilisateur - Email: {}, Rôle: {}", 
                    context, utilisateurDto.getEmail(), utilisateurDto.getRole());
        // 1. Mapper le DTO vers l'Entité
        Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDto);

        // 2. Chiffrer le mot de passe (Utilisation du passwordEncoder injecté)
        if (utilisateurDto.getMotDePasse() != null && !utilisateurDto.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDto.getMotDePasse()));
        }

        // 3. Associer le restaurant
        if (utilisateurDto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(utilisateurDto.getRestaurantId())
                    .orElseThrow(() -> {
                        logger.error("{} Restaurant non trouvé avec l'ID: {}", context, utilisateurDto.getRestaurantId());
                        return new RuntimeException("Restaurant non trouvé avec l'ID : " + utilisateurDto.getRestaurantId());
                    });
            utilisateur.setRestaurant(restaurant);
        }

        // 5. Sauvegarder l'entité
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        logger.info("{} Utilisateur sauvegardé avec succès. ID: {}, Email: {}", context, savedUtilisateur.getId(), savedUtilisateur.getEmail());

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
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les utilisateurs", context);
        List<UtilisateurDto> utilisateurs = utilisateurRepository.findAll()
                .stream()
                .map(user -> {
                    UtilisateurDto dto = utilisateurMapper.toDto(user);
                    dto.setMotDePasse(null); // Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
        logger.info("{} {} utilisateurs récupérés avec succès", context, utilisateurs.size());
        return utilisateurs;
    }

    @Override
    public UtilisateurDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de l'utilisateur avec l'ID: {}", context, id);
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Utilisateur non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Utilisateur avec avec le code "+ id +" est introuvable ");
                });

        UtilisateurDto dto = utilisateurMapper.toDto(utilisateur);
        dto.setMotDePasse(null); // Masquer le mot de passe
        logger.info("{} Utilisateur ID: {} récupéré avec succès - Email: {}", context, id, utilisateur.getEmail());
        return dto;
    }

    @Override
    public UtilisateurDto update(Long id, UtilisateurDto utilisateurDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour de l'utilisateur ID: {}", context, id);
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Utilisateur non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
                });

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
            // 1. On cherche l'OBJET complet en base
            Restaurant restaurant = restaurantRepository.findById(utilisateurDto.getRestaurantId())
                    .orElseThrow(() -> {
                        logger.error("{} Restaurant ID {} non trouvé pour l'utilisateur {}", context, utilisateurDto.getRestaurantId(), id);
                        return new RuntimeException("Restaurant introuvable avec l'ID : " + utilisateurDto.getRestaurantId());
                    });

            // 2. LIGNE ESSENTIELLE : On lie le restaurant à l'utilisateur
            utilisateur.setRestaurant(restaurant); 
            
            // 3. Traçabilité
            logger.info("{} Utilisateur ID: {} rattaché avec succès au restaurant ID: {}", context, id, restaurant.getId());
        }

        if (utilisateurDto.getRole() != null) {
            utilisateur.setRole(utilisateurDto.getRole());
        }

        if (utilisateurDto.getDateEmbauche() != null) {
            utilisateur.setDateEmbauche(utilisateurDto.getDateEmbauche());
        }

        // Sauvegarde
        Utilisateur updatedUtilisateur = utilisateurRepository.save(utilisateur);
        logger.info("{} Utilisateur ID: {} mis à jour avec succès", context, id);

        UtilisateurDto resultDto = utilisateurMapper.toDto(updatedUtilisateur);
        resultDto.setMotDePasse(null); // Ne jamais renvoyer le mot de passe

        return resultDto;
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de l'utilisateur ID: {}", context, id);
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Utilisateur non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
                });

        utilisateurRepository.delete(utilisateur);
        logger.info("{} Utilisateur ID: {} supprimé avec succès", context, id);
    }

    @Override
    public List<UtilisateurDto> findByRoleType(RoleType role) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des utilisateurs par rôle: {}", context, role);
        List<UtilisateurDto> utilisateurs = utilisateurRepository.findByRole(role)
                .stream()
                .map(personnel -> {
                    UtilisateurDto dto = utilisateurMapper.toDto(personnel);
                    dto.setMotDePasse(null); //  Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
        logger.info("{} {} utilisateurs avec le rôle {} récupérés avec succès", context, utilisateurs.size(), role);
        return utilisateurs;
    }

    @Override
    public List<UtilisateurDto> search(String keyword) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche d'utilisateurs par mot-clé: {}", context, keyword);
        List<UtilisateurDto> utilisateurs = utilisateurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(personnel -> {
                    UtilisateurDto dto = utilisateurMapper.toDto(personnel);
                    dto.setMotDePasse(null); // Masquer les mots de passe
                    return dto;
                })
                .collect(Collectors.toList());
        logger.info("{} {} utilisateurs trouvés pour le mot-clé: {}", context, utilisateurs.size(), keyword);
        return utilisateurs;
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de changement de mot de passe pour l'utilisateur ID: {}", context, id);
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Utilisateur non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
                });

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(oldPassword, utilisateur.getMotDePasse())) {
            logger.warn("{} Tentative de changement de mot de passe avec un ancien mot de passe incorrect pour l'utilisateur ID: {}", context, id);
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }

        // Validation du nouveau mot de passe (optionnel)
        if (newPassword == null || newPassword.length() < 6) {
            logger.error("{} Nouveau mot de passe invalide (moins de 6 caractères) pour l'utilisateur ID: {}", context, id);
            throw new RuntimeException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        utilisateur.setMotDePasse(encodedPassword);

        utilisateurRepository.save(utilisateur);
        logger.info("{} Mot de passe modifié avec succès pour l'utilisateur ID: {}", context, id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de réinitialisation de mot de passe pour l'utilisateur ID: {}", context, id);
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Utilisateur non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
                });

        // Validation du nouveau mot de passe (optionnel)
        if (newPassword == null || newPassword.length() < 6) {
            logger.error("{} Mot de passe invalide (moins de 6 caractères) pour l'utilisateur ID: {}", context, id);
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        utilisateur.setMotDePasse(encodedPassword);

        utilisateurRepository.save(utilisateur);
        logger.info("{} Mot de passe réinitialisé avec succès pour l'utilisateur ID: {}", context, id);
    }

    @Override
    public UtilisateurDto findByEmail(String email) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de l'utilisateur avec l'email: {}", context, email);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("{} Utilisateur non trouvé avec l'email: {}", context, email);
                    return new RuntimeException("Utilisateur avec l'email " + email + " est introuvable");
                });

        UtilisateurDto dto = utilisateurMapper.toDto(utilisateur);
        dto.setMotDePasse(null);
        return dto;
    }

    @Override
    public UtilisateurDto getCurrentUtilisateur() {
        String context = LoggingUtils.getLogContext();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        logger.info("{} Récupération des informations de l'utilisateur connecté : {}", context, email);
        return findByEmail(email);
    }

    @Override
    public UtilisateurDto updateProfile(UtilisateurDto utilisateurDto) {
        String context = LoggingUtils.getLogContext();
        UtilisateurDto current = getCurrentUtilisateur();
        Long id = current.getId();
        
        logger.info("{} Mise à jour du profil personnel pour l'utilisateur ID: {}", context, id);
        
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Seuls certains champs sont autorisés à la modification par l'utilisateur lui-même
        if (utilisateurDto.getNom() != null && !utilisateurDto.getNom().isEmpty()) {
            utilisateur.setNom(utilisateurDto.getNom());
        }
        if (utilisateurDto.getPrenom() != null && !utilisateurDto.getPrenom().isEmpty()) {
            utilisateur.setPrenom(utilisateurDto.getPrenom());
        }
        if (utilisateurDto.getTelephone() != null && !utilisateurDto.getTelephone().isEmpty()) {
            utilisateur.setTelephone(utilisateurDto.getTelephone());
        }

        Utilisateur updated = utilisateurRepository.save(utilisateur);
        logger.info("{} Profil personnel mis à jour avec succès pour l'ID: {}", context, id);
        
        UtilisateurDto result = utilisateurMapper.toDto(updated);
        result.setMotDePasse(null);
        return result;
    }
}
