package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.CategoryController;
import com.example.BackendProject.dto.CategoryDto;
import com.example.BackendProject.services.implementations.CategoryServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - CategoryController")
class CategoryControllerTest {

        @Mock
        private CategoryServiceImplementation categoryService;

        @InjectMocks
        private CategoryController categoryController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private CategoryDto categoryDto;

        /**
         * Gestionnaire d'exceptions local pour simuler le comportement du
         * BackendProject
         * et transformer les RuntimeException en codes HTTP corrects (400, 404).
         */
        @RestControllerAdvice
        static class TestGlobalExceptionHandler {
                @ExceptionHandler(RuntimeException.class)
                public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
                        if (e.getMessage().contains("non trouvé")) {
                                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
                        }
                        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }

                @ExceptionHandler(IllegalArgumentException.class)
                public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
                        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
        }

        @BeforeEach
        void setUp() {
                // CORRECTION : On attache le ControllerAdvice au mockMvc
                mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                                .setControllerAdvice(new TestGlobalExceptionHandler())
                                .setMessageConverters(
                                                new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter())
                                .setContentNegotiationManager(
                                                new org.springframework.web.accept.ContentNegotiationManager(
                                                                new org.springframework.web.accept.FixedContentNegotiationStrategy(
                                                                                org.springframework.http.MediaType.APPLICATION_JSON)))
                                .build();

                objectMapper = new ObjectMapper();

                categoryDto = new CategoryDto();
                categoryDto.setId(1L);
                categoryDto.setNom("Entrées");
                categoryDto.setDescription("Délicieuses entrées");
                categoryDto.setOrdreAffichage(1);
        }

        // ==================== Tests CREATE ====================

        @Test
        @DisplayName("POST /api/categories - Créer une catégorie avec succès")
        void testCreateCategory_Success() throws Exception {
                when(categoryService.save(any(CategoryDto.class))).thenReturn(categoryDto);

                mockMvc.perform(post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nom").value("Entrées"));

                verify(categoryService, times(1)).save(any(CategoryDto.class));
        }

        @Test
        @DisplayName("POST /api/categories - Échec création avec données invalides (400)")
        void testCreateCategory_InvalidData() throws Exception {
                when(categoryService.save(any(CategoryDto.class)))
                                .thenThrow(new RuntimeException("Le nom de la catégorie est obligatoire"));

                mockMvc.perform(post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryDto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/categories - Échec création avec menu inexistant (400)")
        void testCreateCategory_MenuNotFound() throws Exception {
                when(categoryService.save(any(CategoryDto.class)))
                                .thenThrow(new RuntimeException("Menu non trouvé avec l'ID : 999"));

                mockMvc.perform(post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryDto)))
                                .andExpect(status().isBadRequest());
        }

        // ==================== Tests READ ALL ====================

        @Test
        @DisplayName("GET /api/categories - Récupérer toutes les catégories")
        void testGetAllCategories_Success() throws Exception {
                CategoryDto category2 = new CategoryDto();
                category2.setId(2L);
                category2.setNom("Plats principaux");

                List<CategoryDto> categories = Arrays.asList(categoryDto, category2);
                when(categoryService.getAll()).thenReturn(categories);

                mockMvc.perform(get("/api/categories")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].nom").value("Entrées"));
        }

        // ==================== Tests READ BY ID ====================

        @Test
        @DisplayName("GET /api/categories/{id} - Récupérer une catégorie par ID")
        void testGetCategoryById_Success() throws Exception {
                when(categoryService.getById(1L)).thenReturn(categoryDto);

                mockMvc.perform(get("/api/categories/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("GET /api/categories/{id} - Catégorie non trouvée (404)")
        void testGetCategoryById_NotFound() throws Exception {
                when(categoryService.getById(999L))
                                .thenThrow(new RuntimeException("Catégorie non trouvée avec l'ID : 999"));

                mockMvc.perform(get("/api/categories/999"))
                                .andExpect(status().isNotFound());
        }

        // ==================== Tests UPDATE ====================

        @Test
        @DisplayName("PUT /api/categories/{id} - Mettre à jour une catégorie")
        void testUpdateCategory_Success() throws Exception {
                CategoryDto updatedCategory = new CategoryDto();
                updatedCategory.setId(1L);
                updatedCategory.setNom("Entrées Chaudes");

                when(categoryService.update(eq(1L), any(CategoryDto.class))).thenReturn(updatedCategory);

                mockMvc.perform(put("/api/categories/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedCategory)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nom").value("Entrées Chaudes"));
        }

        @Test
        @DisplayName("PUT /api/categories/{id} - Mise à jour échouée (404)")
        void testUpdateCategory_NotFound() throws Exception {
                when(categoryService.update(eq(999L), any(CategoryDto.class)))
                                .thenThrow(new RuntimeException("Catégorie non trouvée avec l'ID : 999"));

                mockMvc.perform(put("/api/categories/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryDto)))
                                .andExpect(status().isNotFound());
        }

        // ==================== Tests DELETE ====================

        @Test
        @DisplayName("DELETE /api/categories/{id} - Supprimer une catégorie")
        void testDeleteCategory_Success() throws Exception {
                doNothing().when(categoryService).delete(1L);

                mockMvc.perform(delete("/api/categories/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("DELETE /api/categories/{id} - Suppression échouée (404)")
        void testDeleteCategory_NotFound() throws Exception {
                doThrow(new RuntimeException("Catégorie non trouvée avec l'ID : 999"))
                                .when(categoryService).delete(999L);

                mockMvc.perform(delete("/api/categories/999"))
                                .andExpect(status().isNotFound());
        }

        // ==================== Tests REORDER ====================

        @Test
        @DisplayName("PATCH /api/categories/reorder/{menuId} - Réorganiser les catégories")
        void testReorderCategories_Success() throws Exception {
                List<Long> categoryIds = Arrays.asList(2L, 1L);
                doNothing().when(categoryService).reorderCategories(eq(1L), anyList());

                mockMvc.perform(patch("/api/categories/reorder/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryIds)))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("PATCH /api/categories/reorder/{menuId} - Échec (400)")
        void testReorderCategories_InvalidCategory() throws Exception {
                List<Long> categoryIds = Arrays.asList(1L, 999L);
                doThrow(new RuntimeException("Catégorie non trouvée avec l'ID : 999"))
                                .when(categoryService).reorderCategories(eq(1L), anyList());

                mockMvc.perform(patch("/api/categories/reorder/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryIds)))
                                .andExpect(status().isBadRequest());
        }
}