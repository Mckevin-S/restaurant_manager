package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.PromotionDto;
import com.example.BackendProject.entities.Promotion;
import com.example.BackendProject.mappers.PromotionMapper;
import com.example.BackendProject.repository.PromotionRepository;
import com.example.BackendProject.services.implementations.PromotionServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Promotion")
class PromotionServiceImplementationTest {

    @Mock
    private PromotionRepository repository;

    @Mock
    private PromotionMapper mapper;

    @InjectMocks
    private PromotionServiceImplementation service;

    private Promotion promotion;
    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotion.setId(1L);
        promotion.setNom("Solde Été");
        promotion.setDateExpiration(new java.sql.Date(System.currentTimeMillis() + 10));
        promotionDto = new PromotionDto();
        promotionDto.setId(1L);
        promotionDto.setNom("Solde Été");
    }

    // ==================== TEST SAVE ====================

    @Test
    @DisplayName("Save - Succès de la création d'une promotion")
    void save_ShouldReturnSavedDto() {
        when(mapper.toEntity(any(PromotionDto.class))).thenReturn(promotion);
        when(repository.save(any(Promotion.class))).thenReturn(promotion);
        when(mapper.toDto(any(Promotion.class))).thenReturn(promotionDto);

        PromotionDto result = service.save(promotionDto);

        assertNotNull(result);
        assertEquals("Solde Été", result.getNom());
        verify(repository).save(any(Promotion.class));
    }

    // ==================== TEST FIND ALL & BY ID ====================

    @Test
    @DisplayName("FindAll - Récupérer la liste complète des promotions")
    void findAll_ShouldReturnList() {
        when(repository.findAll()).thenReturn(Arrays.asList(promotion, new Promotion()));
        when(mapper.toDto(any(Promotion.class))).thenReturn(promotionDto);

        List<PromotionDto> results = service.findAll();

        assertEquals(2, results.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("FindById - Succès quand la promotion existe")
    void findById_ShouldReturnDto_WhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(promotion));
        when(mapper.toDto(promotion)).thenReturn(promotionDto);

        PromotionDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("FindById - Erreur quand l'ID est inconnu")
    void findById_ShouldThrowException_WhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> service.findById(99L));
        assertTrue(exception.getMessage().contains("Promotion introuvable"));
    }

    // ==================== TEST UPDATE ====================

    @Test
    @DisplayName("Update - Succès de la mise à jour")
    void update_ShouldSaveUpdatedEntity_WhenExists() {
        PromotionDto updateDto = new PromotionDto();
        updateDto.setNom("Solde Hiver");

        when(repository.findById(1L)).thenReturn(Optional.of(promotion));
        when(mapper.toEntity(updateDto)).thenReturn(promotion);
        when(repository.save(any(Promotion.class))).thenReturn(promotion);
        when(mapper.toDto(any(Promotion.class))).thenReturn(updateDto);

        PromotionDto result = service.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Solde Hiver", result.getNom());
        verify(repository).save(promotion);
    }

    // ==================== TEST DELETE ====================

    @Test
    @DisplayName("Delete - Succès de la suppression")
    void delete_ShouldCallRepository_WhenIdExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete - Erreur si l'ID n'existe pas")
    void delete_ShouldThrowException_WhenIdInconnu() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(any());
    }
}
