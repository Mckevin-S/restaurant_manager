package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.OptionItemDto;
import com.example.BackendProject.entities.OptionItem;
import com.example.BackendProject.mappers.OptionItemMapper;
import com.example.BackendProject.repository.OptionItemRepository;
import com.example.BackendProject.services.implementations.OptionItemServiceImplementation;
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
@DisplayName("Tests de Service - OptionItem (Suppléments)")
class OptionItemServiceImplementationTest {

    @Mock
    private OptionItemRepository optionItemRepository;

    @Mock
    private OptionItemMapper optionItemMapper;

    @InjectMocks
    private OptionItemServiceImplementation optionItemService;

    private OptionItem optionItem;
    private OptionItemDto optionItemDto;

    @BeforeEach
    void setUp() {
        optionItem = new OptionItem();
        optionItem.setId(1L);
        optionItem.setNom("Supplément Fromage");
        optionItem.setPrixSupplementaire(new BigDecimal("500"));

        optionItemDto = new OptionItemDto();
        optionItemDto.setId(1L);
        optionItemDto.setNom("Supplément Fromage");
        optionItemDto.setPrixSupplementaire(new BigDecimal("500"));
    }

    // ==================== TESTS SAVE ====================

    @Test
    @DisplayName("Save - Devrait sauvegarder et retourner le DTO")
    void save_ShouldReturnSavedDto() {
        when(optionItemMapper.toEntity(any(OptionItemDto.class))).thenReturn(optionItem);
        when(optionItemRepository.save(any(OptionItem.class))).thenReturn(optionItem);
        when(optionItemMapper.toDto(any(OptionItem.class))).thenReturn(optionItemDto);

        OptionItemDto result = optionItemService.save(optionItemDto);

        assertNotNull(result);
        assertEquals("Supplément Fromage", result.getNom());
        verify(optionItemRepository, times(1)).save(any(OptionItem.class));
    }

    // ==================== TESTS UPDATE ====================

    @Test
    @DisplayName("Update - Devrait modifier les champs et sauvegarder")
    void update_ShouldUpdateFields_WhenExists() {
        OptionItemDto updateDto = new OptionItemDto();
        updateDto.setNom("Supplément Bacon");
        updateDto.setPrixSupplementaire(new BigDecimal("700"));

        when(optionItemRepository.findById(1L)).thenReturn(Optional.of(optionItem));
        when(optionItemRepository.save(any(OptionItem.class))).thenReturn(optionItem);
        when(optionItemMapper.toDto(any(OptionItem.class))).thenReturn(updateDto);

        OptionItemDto result = optionItemService.update(1L, updateDto);

        assertEquals("Supplément Bacon", optionItem.getNom());
        assertEquals(new BigDecimal("700"), optionItem.getPrixSupplementaire());
        verify(optionItemRepository).save(optionItem);
    }

    @Test
    @DisplayName("Update - Devrait lever une exception si l'ID n'existe pas")
    void update_ShouldThrowException_WhenNotFound() {
        when(optionItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> optionItemService.update(99L, optionItemDto));
        verify(optionItemRepository, never()).save(any());
    }

    // ==================== TESTS GET ALL & BY ID ====================

    @Test
    @DisplayName("GetAll - Devrait retourner une liste de DTO")
    void getAll_ShouldReturnDtoList() {
        List<OptionItem> entities = Arrays.asList(optionItem, new OptionItem());
        when(optionItemRepository.findAll()).thenReturn(entities);
        when(optionItemMapper.toDto(any(OptionItem.class))).thenReturn(optionItemDto);

        List<OptionItemDto> results = optionItemService.getAll();

        assertEquals(2, results.size());
        verify(optionItemRepository).findAll();
    }

    @Test
    @DisplayName("GetById - Succès")
    void getById_ShouldReturnDto_WhenFound() {
        when(optionItemRepository.findById(1L)).thenReturn(Optional.of(optionItem));
        when(optionItemMapper.toDto(optionItem)).thenReturn(optionItemDto);

        OptionItemDto result = optionItemService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ==================== TESTS DELETE ====================

    @Test
    @DisplayName("Delete - Devrait appeler le repository pour suppression")
    void delete_ShouldInvokeRepository() {
        // Arrange
        Long id = 1L;
        // On simule que l'objet existe pour passer la validation interne du service
        // Note: Utilisez existsById ou findById selon ce que votre service utilise
        when(optionItemRepository.existsById(id)).thenReturn(true);

        // Act
        optionItemService.delete(id);

        // Assert
        verify(optionItemRepository, times(1)).deleteById(id);
    }
}
