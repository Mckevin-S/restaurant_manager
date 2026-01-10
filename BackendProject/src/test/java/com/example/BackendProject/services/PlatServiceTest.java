package com.example.BackendProject.services;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.exceptions.RessourceNonTrouveeException;
import com.example.BackendProject.mappers.PlatMapper;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.services.implementations.PlatServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le service PlatServiceImplementation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Plat")
class PlatServiceTest {

    @Mock
    private PlatRepository platRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PlatMapper platMapper;

    @InjectMocks
    private PlatServiceImplementation platService;

    private Plat plat;
    private PlatDto platDto;
    private Category category;

    @BeforeEach
    void setUp() {
        // Initialisation des données de test
        category = new Category();
        category.setId(1L);
        category.setNom("Entrées");

        plat = new Plat();
        plat.setId(1L);
        plat.setNom("Salade César");
        plat.setDescription("Salade fraîche avec poulet grillé");
        plat.setPrix(BigDecimal.valueOf(12.50));
        plat.setDisponibilite(true);
        plat.setCategory(category);

        platDto = new PlatDto();
        platDto.setId(1L);
        platDto.setNom("Salade César");
        platDto.setDescription("Salade fraîche avec poulet grillé");
        platDto.setPrix(BigDecimal.valueOf(12.50));
        platDto.setDisponibilite(true);
        platDto.setCategory(1L);
    }

    @Test
    @DisplayName("Devrait créer un plat avec succès")
    void devraitCreerPlatAvecSucces() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(platMapper.toEntity(platDto)).thenReturn(plat);
        when(platRepository.save(any(Plat.class))).thenReturn(plat);
        when(platMapper.toDto(plat)).thenReturn(platDto);

        // Act
        PlatDto result = platService.save(platDto);

        // Assert
        assertNotNull(result);
        assertEquals("Salade César", result.getNom());
        assertEquals(BigDecimal.valueOf(12.50), result.getPrix());
        verify(platRepository, times(1)).save(any(Plat.class));
    }

    @Test
    @DisplayName("Devrait lever une exception si la catégorie n'existe pas")
    void devraitLeverExceptionSiCategorieInexistante() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RessourceNonTrouveeException.class, () -> {
            platService.save(platDto);
        });
        verify(platRepository, never()).save(any(Plat.class));
    }

    @Test
    @DisplayName("Devrait récupérer tous les plats")
    void devraitRecupererTousLesPlats() {
        // Arrange
        List<Plat> plats = Arrays.asList(plat);
        when(platRepository.findAll()).thenReturn(plats);
        when(platMapper.toDto(any(Plat.class))).thenReturn(platDto);

        // Act
        List<PlatDto> results = platService.getAll();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Salade César", results.get(0).getNom());
        verify(platRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait récupérer un plat par ID")
    void devraitRecupererPlatParId() {
        // Arrange
        when(platRepository.findById(1L)).thenReturn(Optional.of(plat));
        when(platMapper.toDto(plat)).thenReturn(platDto);

        // Act
        PlatDto result = platService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Salade César", result.getNom());
        verify(platRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si le plat n'existe pas")
    void devraitLeverExceptionSiPlatInexistant() {
        // Arrange
        when(platRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RessourceNonTrouveeException.class, () -> {
            platService.getById(999L);
        });
    }

    @Test
    @DisplayName("Devrait mettre à jour un plat")
    void devraitMettreAJourPlat() {
        // Arrange
        PlatDto updateDto = new PlatDto();
        updateDto.setNom("Salade César Royale");
        updateDto.setPrix(BigDecimal.valueOf(15.00));
        updateDto.setCategory(1L);

        when(platRepository.findById(1L)).thenReturn(Optional.of(plat));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(platRepository.save(any(Plat.class))).thenReturn(plat);
        when(platMapper.toDto(any(Plat.class))).thenReturn(updateDto);

        // Act
        PlatDto result = platService.update(1L, updateDto);

        // Assert
        assertNotNull(result);
        verify(platRepository, times(1)).save(any(Plat.class));
    }

    @Test
    @DisplayName("Devrait supprimer un plat")
    void devraitSupprimerPlat() {
        // Arrange
        when(platRepository.existsById(1L)).thenReturn(true);
        doNothing().when(platRepository).deleteById(1L);

        // Act
        platService.delete(1L);

        // Assert
        verify(platRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception lors de la suppression d'un plat inexistant")
    void devraitLeverExceptionSupprimerPlatInexistant() {
        // Arrange
        when(platRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(RessourceNonTrouveeException.class, () -> {
            platService.delete(999L);
        });
        verify(platRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Devrait récupérer uniquement les plats disponibles")
    void devraitRecupererPlatsDisponibles() {
        // Arrange
        when(platRepository.findByDisponibiliteTrue()).thenReturn(Arrays.asList(plat));
        when(platMapper.toDto(any(Plat.class))).thenReturn(platDto);

        // Act
        List<PlatDto> results = platService.getMenuActif();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isDisponibilite());
        verify(platRepository, times(1)).findByDisponibiliteTrue();
    }

    @Test
    @DisplayName("Devrait modifier la disponibilité d'un plat")
    void devraitModifierDisponibilite() {
        // Arrange
        when(platRepository.findById(1L)).thenReturn(Optional.of(plat));
        when(platRepository.save(any(Plat.class))).thenReturn(plat);
        when(platMapper.toDto(any(Plat.class))).thenReturn(platDto);

        // Act
        PlatDto result = platService.modifierDisponibilite(1L, false);

        // Assert
        assertNotNull(result);
        verify(platRepository, times(1)).save(any(Plat.class));
    }
}
