package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.RecetteDto;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.mappers.RecetteMapper;
import com.example.BackendProject.repository.RecetteRepository;
import com.example.BackendProject.services.implementations.RecetteServiceImplementation;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Recette (Fiches Techniques)")
class RecetteServiceImplementationTest {

    @Mock
    private RecetteRepository recetteRepository;

    @Mock
    private RecetteMapper recetteMapper;

    @InjectMocks
    private RecetteServiceImplementation service;

    private Recette recette;
    private RecetteDto recetteDto;

    @BeforeEach
    void setUp() {
        recette = new Recette();
        recette.setId(1L);
        recette.setNom("Recette Burger Classic");

        recetteDto = new RecetteDto();
        recetteDto.setId(1L);
        recetteDto.setNom("Recette Burger Classic");
    }

    // ==================== TEST SAVE ====================

    @Test
    @DisplayName("Save - Devrait sauvegarder et retourner le DTO")
    void save_ShouldPersistAndReturnDto() {
        // Arrange
        when(recetteMapper.toEntity(any(RecetteDto.class))).thenReturn(recette);
        when(recetteRepository.save(any(Recette.class))).thenReturn(recette);
        when(recetteMapper.toDto(any(Recette.class))).thenReturn(recetteDto);

        // Act
        RecetteDto result = service.save(recetteDto);

        // Assert
        assertNotNull(result);
        assertEquals("Recette Burger Classic", result.getNom());
        verify(recetteRepository).save(recette);
    }

    // ==================== TESTS RÉCUPÉRATION ====================

    @Test
    @DisplayName("GetById - Succès")
    void getById_ShouldReturnDto_WhenFound() {
        // Arrange
        when(recetteRepository.findById(1L)).thenReturn(Optional.of(recette));
        when(recetteMapper.toDto(recette)).thenReturn(recetteDto);

        // Act
        RecetteDto result = service.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("GetById - Devrait lancer une exception si non trouvé")
    void getById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(recetteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    @DisplayName("GetByPlat - Devrait retourner la liste pour un plat donné")
    void getByPlat_ShouldReturnList() {
        // Arrange
        Long platId = 10L;
        when(recetteRepository.findByPlatId(platId)).thenReturn(Arrays.asList(recette));
        when(recetteMapper.toDto(any(Recette.class))).thenReturn(recetteDto);

        // Act
        List<RecetteDto> results = service.getByPlat(platId);

        // Assert
        assertEquals(1, results.size());
        verify(recetteRepository).findByPlatId(platId);
    }

    @Test
    @DisplayName("GetAll - Devrait retourner toutes les recettes")
    void getAll_ShouldReturnCompleteList() {
        // Arrange
        when(recetteRepository.findAll()).thenReturn(Arrays.asList(recette, new Recette()));
        when(recetteMapper.toDto(any(Recette.class))).thenReturn(recetteDto);

        // Act
        List<RecetteDto> results = service.getAll();

        // Assert
        assertEquals(2, results.size());
        verify(recetteRepository).findAll();
    }

    // ==================== TEST SUPPRESSION ====================

    @Test
    @DisplayName("Delete - Devrait appeler le repository")
    void delete_ShouldInvokeRepository() {
        // Act
        service.delete(1L);

        // Assert
        verify(recetteRepository).deleteById(1L);
    }
}
