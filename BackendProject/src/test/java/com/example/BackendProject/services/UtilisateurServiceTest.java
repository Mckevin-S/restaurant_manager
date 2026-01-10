package com.example.BackendProject.services;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.exceptions.RessourceNonTrouveeException;
import com.example.BackendProject.mappers.UtilisateurMapper;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.implementations.UtilisateurServiceImplementation;
import com.example.BackendProject.utils.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Utilisateur")
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurMapper utilisateurMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurServiceImplementation utilisateurService;

    private Utilisateur utilisateur;
    private UtilisateurDto utilisateurDto;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setEmail("jean.dupont@restaurant.com");
        utilisateur.setMotDePasse("hashedPassword");
        utilisateur.setTelephone("+237690000000");
        utilisateur.setRole(RoleType.SERVEUR);
        utilisateur.setDateEmbauche(LocalDate.now());

        utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(1L);
        utilisateurDto.setNom("Dupont");
        utilisateurDto.setPrenom("Jean");
        utilisateurDto.setEmail("jean.dupont@restaurant.com");
        utilisateurDto.setMotDePasse("password123");
        utilisateurDto.setTelephone("+237690000000");
        utilisateurDto.setRole(RoleType.SERVEUR);
        utilisateurDto.setDateEmbauche(LocalDate.now());
    }

    @Test
    @DisplayName("Devrait créer un utilisateur avec succès")
    void devraitCreerUtilisateurAvecSucces() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(utilisateurMapper.toEntity(utilisateurDto)).thenReturn(utilisateur);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.save(utilisateurDto);

        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        assertEquals("jean.dupont@restaurant.com", result.getEmail());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Devrait encoder le mot de passe lors de la création")
    void devraitEncoderMotDePasse() {
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(utilisateurMapper.toEntity(utilisateurDto)).thenReturn(utilisateur);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        utilisateurService.save(utilisateurDto);

        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Devrait récupérer tous les utilisateurs")
    void devraitRecupererTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = Arrays.asList(utilisateur);
        when(utilisateurRepository.findAll()).thenReturn(utilisateurs);
        when(utilisateurMapper.toDto(any(Utilisateur.class))).thenReturn(utilisateurDto);

        List<UtilisateurDto> results = utilisateurService.getAll();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(utilisateurRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait récupérer un utilisateur par ID")
    void devraitRecupererUtilisateurParId() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Dupont", result.getNom());
        verify(utilisateurRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si l'utilisateur n'existe pas")
    void devraitLeverExceptionSiUtilisateurInexistant() {
        when(utilisateurRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RessourceNonTrouveeException.class, () -> {
            utilisateurService.getById(999L);
        });
    }

    @Test
    @DisplayName("Devrait récupérer un utilisateur par email")
    void devraitRecupererUtilisateurParEmail() {
        when(utilisateurRepository.findByEmail("jean.dupont@restaurant.com"))
                .thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.findByEmail("jean.dupont@restaurant.com");

        assertNotNull(result);
        assertEquals("jean.dupont@restaurant.com", result.getEmail());
        verify(utilisateurRepository, times(1)).findByEmail("jean.dupont@restaurant.com");
    }

    @Test
    @DisplayName("Devrait récupérer les utilisateurs par rôle")
    void devraitRecupererUtilisateursParRole() {
        when(utilisateurRepository.findByRole(RoleType.SERVEUR))
                .thenReturn(Arrays.asList(utilisateur));
        when(utilisateurMapper.toDto(any(Utilisateur.class))).thenReturn(utilisateurDto);

        List<UtilisateurDto> results = utilisateurService.findByRoleType(RoleType.SERVEUR);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(RoleType.SERVEUR, results.get(0).getRole());
        verify(utilisateurRepository, times(1)).findByRole(RoleType.SERVEUR);
    }

    @Test
    @DisplayName("Devrait rechercher des utilisateurs par mot-clé")
    void devraitRechercherUtilisateurs() {
        when(utilisateurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase("Dupont", "Dupont"))
                .thenReturn(Arrays.asList(utilisateur));
        when(utilisateurMapper.toDto(any(Utilisateur.class))).thenReturn(utilisateurDto);

        List<UtilisateurDto> results = utilisateurService.search("Dupont");

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(utilisateurRepository, times(1))
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase("Dupont", "Dupont");
    }

    @Test
    @DisplayName("Devrait mettre à jour un utilisateur")
    void devraitMettreAJourUtilisateur() {
        UtilisateurDto updateDto = new UtilisateurDto();
        updateDto.setNom("Martin");
        updateDto.setPrenom("Pierre");

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        when(utilisateurMapper.toDto(any(Utilisateur.class))).thenReturn(updateDto);

        UtilisateurDto result = utilisateurService.update(1L, updateDto);

        assertNotNull(result);
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Devrait changer le mot de passe avec vérification")
    void devraitChangerMotDePasse() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        utilisateurService.changePassword(1L, "oldPassword", "newPassword");

        verify(passwordEncoder, times(1)).matches("oldPassword", "hashedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Devrait lever une exception si l'ancien mot de passe est incorrect")
    void devraitLeverExceptionSiAncienMotDePasseIncorrect() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            utilisateurService.changePassword(1L, "wrongPassword", "newPassword");
        });

        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Devrait réinitialiser le mot de passe sans vérification")
    void devraitReinitialiserMotDePasse() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        utilisateurService.resetPassword(1L, "newPassword");

        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Devrait supprimer un utilisateur")
    void devraitSupprimerUtilisateur() {
        when(utilisateurRepository.existsById(1L)).thenReturn(true);
        doNothing().when(utilisateurRepository).deleteById(1L);

        utilisateurService.delete(1L);

        verify(utilisateurRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception lors de la suppression d'un utilisateur inexistant")
    void devraitLeverExceptionSupprimerUtilisateurInexistant() {
        when(utilisateurRepository.existsById(999L)).thenReturn(false);

        assertThrows(RessourceNonTrouveeException.class, () -> {
            utilisateurService.delete(999L);
        });

        verify(utilisateurRepository, never()).deleteById(999L);
    }
}
