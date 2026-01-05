package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.mappers.UtilisateurMapper;
import com.example.BackendProject.repository.RestaurantRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Gestion des Utilisateurs")
class UtilisateurServiceImplementationTest {

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private UtilisateurMapper utilisateurMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurServiceImplementation utilisateurService;

    private Utilisateur utilisateur;
    private UtilisateurDto utilisateurDto;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setNom("Le Gourmet");

        utilisateur = new Utilisateur();
        utilisateur.setId(10L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setMotDePasse("encoded_password");
        utilisateur.setRole(RoleType.MANAGER);

        utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(10L);
        utilisateurDto.setNom("Dupont");
        utilisateurDto.setMotDePasse("raw_password");
        utilisateurDto.setRestaurantId(1L);
        utilisateurDto.setRole(RoleType.MANAGER);
    }

    // ==================== TEST SAUVEGARDE ====================

    @Test
    @DisplayName("Save - Succès avec chiffrement du mot de passe")
    void save_ShouldHashPasswordAndReturnDtoWithoutPassword() {
        // Arrange
        when(utilisateurMapper.toEntity(any())).thenReturn(utilisateur);
        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password");
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);
        when(utilisateurMapper.toDto(any())).thenReturn(utilisateurDto);

        // Act
        UtilisateurDto result = utilisateurService.save(utilisateurDto);

        // Assert
        assertNotNull(result);
        assertNull(result.getMotDePasse(), "Le mot de passe doit être masqué dans le retour");
        verify(passwordEncoder).encode("raw_password");
        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    // ==================== TEST RÉCUPÉRATION ====================

    @Test
    @DisplayName("GetById - Doit masquer le mot de passe")
    void getById_ShouldHidePassword() {
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.getById(10L);

        assertNull(result.getMotDePasse());
        verify(utilisateurRepository).findById(10L);
    }

    // ==================== TEST MISE À JOUR ====================

    @Test
    @DisplayName("Update - Modifier le nom et vérifier la persistance")
    void update_ShouldUpdateFieldsSuccessfully() {
        // Arrange
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(utilisateur));

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        when(utilisateurRepository.save(any())).thenReturn(utilisateur);
        when(utilisateurMapper.toDto(any())).thenReturn(utilisateurDto);

        utilisateurDto.setNom("Martin");
        // L'ID 1L est déjà dans utilisateurDto.restaurantId depuis le setUp()

        // Act
        UtilisateurDto result = utilisateurService.update(10L, utilisateurDto);

        // Assert
        assertEquals("Martin", utilisateur.getNom());
        verify(utilisateurRepository).save(utilisateur);
    }

    // ==================== TEST MOT DE PASSE ====================

    @Test
    @DisplayName("ChangePassword - Succès si l'ancien mot de passe match")
    void changePassword_ShouldSucceed_WhenOldPasswordMatches() {
        // Arrange
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("old_pass", "encoded_password")).thenReturn(true);
        when(passwordEncoder.encode("new_pass")).thenReturn("new_encoded_password");

        // Act
        utilisateurService.changePassword(10L, "old_pass", "new_pass");

        // Assert
        assertEquals("new_encoded_password", utilisateur.getMotDePasse());
        verify(utilisateurRepository).save(utilisateur);
    }

    @Test
    @DisplayName("ChangePassword - Échec si l'ancien mot de passe est faux")
    void changePassword_ShouldThrow_WhenOldPasswordIsWrong() {
        // Arrange
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrong_pass", "encoded_password")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> utilisateurService.changePassword(10L, "wrong_pass", "new_pass"));

        assertEquals("L'ancien mot de passe est incorrect", exception.getMessage());
        verify(utilisateurRepository, never()).save(any());
    }

    // ==================== TEST SUPPRESSION ====================

    @Test
    @DisplayName("Delete - Succès")
    void delete_ShouldCallDelete() {
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(utilisateur));

        utilisateurService.delete(10L);

        verify(utilisateurRepository).delete(utilisateur);
    }
}
