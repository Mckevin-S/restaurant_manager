package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.RecetteItemDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.entities.RecetteItem;
import com.example.BackendProject.mappers.RecetteItemMapper;
import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.repository.RecetteItemRepository;
import com.example.BackendProject.repository.RecetteRepository;
import com.example.BackendProject.services.implementations.RecetteItemServiceImplementation;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - RecetteItem (Lien Ingrédients)")
class RecetteItemServiceImplementationTest {

    @Mock private RecetteItemRepository recetteItemRepository;
    @Mock private RecetteRepository recetteRepository;
    @Mock private IngredientRepository ingredientRepository;
    @Mock private RecetteItemMapper mapper;

    @InjectMocks
    private RecetteItemServiceImplementation service;

    private Recette recette;
    private Ingredient ingredient;
    private RecetteItem recetteItem;
    private RecetteItemDto recetteItemDto;

    @BeforeEach
    void setUp() {
        recette = new Recette();
        recette.setId(100L);
        recette.setNom("Pâte à Pizza");

        ingredient = new Ingredient();
        ingredient.setId(200L);
        ingredient.setNom("Farine");

        recetteItem = new RecetteItem();
        recetteItem.setId(1L);
        recetteItem.setQuantiteRequise(BigDecimal.valueOf(500.0));
        recetteItem.setRecette(recette);
        recetteItem.setIngredient(ingredient);

        recetteItemDto = new RecetteItemDto();
        recetteItemDto.setId(1L);
        recetteItemDto.setRecette(100L);
        recetteItemDto.setIngredient(200L);
        recetteItemDto.setQuantiteRequise(BigDecimal.valueOf(500.0));
    }

    // ==================== TEST SAVE ====================

    @Test
    @DisplayName("Save - Succès quand la recette et l'ingrédient existent")
    void save_ShouldSucceed_WhenParentsExist() {
        // Arrange
        when(recetteRepository.findById(100L)).thenReturn(Optional.of(recette));
        when(ingredientRepository.findById(200L)).thenReturn(Optional.of(ingredient));
        when(mapper.toEntity(any(RecetteItemDto.class))).thenReturn(recetteItem);
        when(recetteItemRepository.save(any(RecetteItem.class))).thenReturn(recetteItem);
        when(mapper.toDto(any(RecetteItem.class))).thenReturn(recetteItemDto);

        // Act
        RecetteItemDto result = service.save(recetteItemDto);

        // Assert
        assertNotNull(result);
        assertTrue(new BigDecimal("500.0").compareTo(result.getQuantiteRequise()) == 0);
        verify(recetteItemRepository).save(recetteItem);
    }

    @Test
    @DisplayName("Save - Échec si la recette est introuvable")
    void save_ShouldThrow_WhenRecetteNotFound() {
        when(recetteRepository.findById(100L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.save(recetteItemDto));
        assertEquals("Recette introuvable", ex.getMessage());
        verify(recetteItemRepository, never()).save(any());
    }

    // ==================== TESTS RÉCUPÉRATION ====================

    @Test
    @DisplayName("GetByRecette - Retourne les ingrédients d'une recette précise")
    void getByRecette_ShouldReturnList() {
        when(recetteItemRepository.findByRecetteId(100L)).thenReturn(Arrays.asList(recetteItem));
        when(mapper.toDto(any())).thenReturn(recetteItemDto);

        List<RecetteItemDto> results = service.getByRecette(100L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(recetteItemRepository).findByRecetteId(100L);
    }

    @Test
    @DisplayName("GetById - Succès")
    void getById_ShouldReturnDto() {
        when(recetteItemRepository.findById(1L)).thenReturn(Optional.of(recetteItem));
        when(mapper.toDto(recetteItem)).thenReturn(recetteItemDto);

        RecetteItemDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ==================== TEST SUPPRESSION ====================

    @Test
    @DisplayName("Delete - Appelle le repository")
    void delete_ShouldInvokeRepository() {
        // 1. Arrange : On prépare le terrain
        Long id = 1L;
        // On indique au mock que l'élément existe bien
        when(recetteItemRepository.existsById(id)).thenReturn(true);

        // 2. Act : On exécute l'action
        service.delete(id);

        // 3. Assert : On vérifie que la méthode de suppression a bien été appelée
        verify(recetteItemRepository, times(1)).deleteById(id);
    }
}
