package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.MenuDto;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.mappers.MenuMapper;
import com.example.BackendProject.repository.MenuRepository;
import com.example.BackendProject.services.implementations.MenuServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Gestion du Menu")
class MenuServiceImplementationTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuServiceImplementation menuService;

    private Menu menu;
    private MenuDto menuDto;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setId(1L);
        menu.setNom("Menu Gastronomique");
        menu.setCategories(new ArrayList<>());

        menuDto = new MenuDto();
        menuDto.setId(1L);
        menuDto.setNom("Menu Gastronomique");
    }

    // ==================== TESTS SAVE ====================

    @Test
    @DisplayName("Save - Succès quand le nom est valide et unique")
    void save_ShouldSucceed_WhenValid() {
        when(menuRepository.findByNomIgnoreCase("Menu Gastronomique")).thenReturn(Optional.empty());
        when(menuMapper.toEntity(any(MenuDto.class))).thenReturn(menu);
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);
        when(menuMapper.toDto(any(Menu.class))).thenReturn(menuDto);

        MenuDto saved = menuService.save(menuDto);

        assertNotNull(saved);
        assertEquals("Menu Gastronomique", saved.getNom());
        verify(menuRepository).save(any(Menu.class));
    }

    @Test
    @DisplayName("Save - Échec si le nom existe déjà")
    void save_ShouldThrow_WhenNameAlreadyExists() {
        when(menuRepository.findByNomIgnoreCase("Menu Gastronomique")).thenReturn(Optional.of(menu));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> menuService.save(menuDto));
        assertEquals("Un menu avec ce nom existe déjà", ex.getMessage());
        verify(menuRepository, never()).save(any());
    }

    // ==================== TESTS GET & LIST ====================

    @Test
    @DisplayName("GetAll - Devrait retourner une liste")
    void getAll_ShouldReturnList() {
        when(menuRepository.findAll()).thenReturn(Collections.singletonList(menu));
        when(menuMapper.toDto(any())).thenReturn(menuDto);

        List<MenuDto> results = menuService.getAll();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    // ==================== TESTS UPDATE ====================

    @Test
    @DisplayName("Update - Succès")
    void update_ShouldWork_WhenMenuExists() {
        MenuDto updateDto = new MenuDto();
        updateDto.setNom("Menu Midi");

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(menuRepository.save(any())).thenReturn(menu);
        when(menuMapper.toDto(any())).thenReturn(updateDto);

        MenuDto result = menuService.update(1L, updateDto);

        assertEquals("Menu Midi", result.getNom());
        verify(menuRepository).save(menu);
    }

    // ==================== TESTS DELETE ====================

    @Test
    @DisplayName("Delete - Échec si le menu contient des catégories")
    void delete_ShouldThrow_WhenHasCategories() {
        // Ajouter une catégorie fictive pour déclencher l'erreur
        menu.setCategories(Collections.singletonList(new Category()));

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> menuService.delete(1L));
        assertTrue(ex.getMessage().contains("contenant des catégories"));
        verify(menuRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Delete - Succès si vide")
    void delete_ShouldWork_WhenEmpty() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        menuService.delete(1L);

        verify(menuRepository).delete(menu);
    }
}
