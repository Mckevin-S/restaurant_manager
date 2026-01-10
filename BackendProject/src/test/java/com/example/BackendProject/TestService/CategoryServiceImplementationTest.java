package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.CategoryDto;
import com.example.BackendProject.dto.MenuDto;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.mappers.CategoryMapper;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.repository.MenuRepository;
import com.example.BackendProject.services.implementations.CategoryServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - CategoryImplementation")
class CategoryServiceImplementationTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImplementation categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private Menu menu;
    private MenuDto menuDto;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setId(1L);

        menuDto = new MenuDto();
        menuDto.setId(1L);

        category = new Category();
        category.setId(1L);
        category.setNom("Entrées");
        category.setMenu(menu);
        category.setPlats(new ArrayList<>());

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setNom("Entrées");
        categoryDto.setMenuId(menu.getId());
    }

    // ==================== TESTS SAVE ====================

    @Test
    @DisplayName("Sauvegarder une catégorie - Calcul automatique de l'ordre")
    void save_ShouldAutoIncrementOrder_WhenOrderIsNull() {
        categoryDto.setOrdreAffichage(null);

        when(menuRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findMaxOrdreAffichageByMenuId(1L)).thenReturn(5);
        when(categoryMapper.toEntity(any(CategoryDto.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);

        categoryService.save(categoryDto);

        assertEquals(6, categoryDto.getOrdreAffichage());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Sauvegarder une catégorie - Échec si nom vide")
    void save_ShouldThrowException_WhenNameIsEmpty() {
        categoryDto.setNom("");
        Exception exception = assertThrows(RuntimeException.class, () -> categoryService.save(categoryDto));
        assertTrue(exception.getMessage().contains("obligatoire"));
    }

    // ==================== TESTS UPDATE ====================

    @Test
    @DisplayName("Mettre à jour une catégorie")
    void update_ShouldUpdateFields_WhenCategoryExists() {
        CategoryDto updateDto = new CategoryDto();
        updateDto.setNom("Nouveau Nom");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);

        categoryService.update(1L, updateDto);

        assertEquals("Nouveau Nom", category.getNom());
        verify(categoryRepository).save(category);
    }

    // ==================== TESTS DELETE ====================

    @Test
    @DisplayName("Supprimer une catégorie - Échec si contient des plats")
    void delete_ShouldThrowException_WhenCategoryHasPlats() {
        // Simuler une catégorie avec des plats
        category.setPlats(Arrays.asList(new com.example.BackendProject.entities.Plat()));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Exception exception = assertThrows(RuntimeException.class, () -> categoryService.delete(1L));
        assertTrue(exception.getMessage().contains("contenant des plats"));
    }

    @Test
    @DisplayName("Supprimer une catégorie - Succès")
    void delete_ShouldCallDelete_WhenCategoryIsEmpty() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository).delete(category);
    }

    // ==================== TESTS REORDER ====================

    @Test
    @DisplayName("Réorganiser les catégories d'un menu")
    void reorderCategories_ShouldUpdateOrdersSequentially() {
        Long menuId = 1L;
        List<Long> ids = Arrays.asList(1L, 2L);

        Category cat1 = new Category(); cat1.setId(1L); cat1.setMenu(menu);
        Category cat2 = new Category(); cat2.setId(2L); cat2.setMenu(menu);

        when(menuRepository.existsById(menuId)).thenReturn(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(cat2));

        categoryService.reorderCategories(menuId, ids);

        assertEquals(1, cat1.getOrdreAffichage());
        assertEquals(2, cat2.getOrdreAffichage());
        verify(categoryRepository, times(2)).save(any(Category.class));
    }

    @Test
    @DisplayName("Réorganiser - Échec si la catégorie n'appartient pas au menu")
    void reorderCategories_ShouldThrowException_WhenMenuMismatch() {
        Menu otherMenu = new Menu(); otherMenu.setId(99L);
        category.setMenu(otherMenu);

        when(menuRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(RuntimeException.class, () ->
                categoryService.reorderCategories(1L, Arrays.asList(1L))
        );
    }
}
