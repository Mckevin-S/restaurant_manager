package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.MenuController;
import com.example.BackendProject.dto.MenuDto;
import com.example.BackendProject.services.implementations.MenuServiceImplementation;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - MenuController")
class MenuControllerTest {

    @Mock
    private MenuServiceImplementation menuService;

    @InjectMocks
    private MenuController menuController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MenuDto menuDto;

    /**
     * Gestionnaire d'exceptions local pour simuler le comportement du BackendProject
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
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(menuController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        menuDto = new MenuDto();
        menuDto.setId(1L);
        menuDto.setNom("Carte d'Été");
    }

    // ==================== Tests CREATE ====================

    @Test
    @DisplayName("POST /api/menus - Créer un menu avec succès")
    void testCreateMenu_Success() throws Exception {
        when(menuService.save(any(MenuDto.class))).thenReturn(menuDto);

        mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Carte d'Été"));
    }

    @Test
    @DisplayName("POST /api/menus - Échec si erreur service (400)")
    void testCreateMenu_Error() throws Exception {
        when(menuService.save(any(MenuDto.class))).thenThrow(new RuntimeException("Erreur de sauvegarde"));

        mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuDto)))
                .andExpect(status().isBadRequest());
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/menus - Récupérer tous les menus")
    void testGetAllMenus_Success() throws Exception {
        when(menuService.getAll()).thenReturn(Arrays.asList(menuDto));

        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom").value("Carte d'Été"));
    }

    @Test
    @DisplayName("GET /api/menus/{id} - Menu trouvé")
    void testGetMenuById_Success() throws Exception {
        when(menuService.getById(1L)).thenReturn(menuDto);

        mockMvc.perform(get("/api/menus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/menus/{id} - Menu non trouvé (404)")
    void testGetMenuById_NotFound() throws Exception {
        when(menuService.getById(99L)).thenThrow(new RuntimeException("Menu non trouvé"));

        mockMvc.perform(get("/api/menus/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== Tests UPDATE ====================

    @Test
    @DisplayName("PUT /api/menus/{id} - Mettre à jour avec succès")
    void testUpdateMenu_Success() throws Exception {
        when(menuService.update(eq(1L), any(MenuDto.class))).thenReturn(menuDto);

        mockMvc.perform(put("/api/menus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Carte d'Été"));
    }

    // ==================== Tests DELETE ====================

    @Test
    @DisplayName("DELETE /api/menus/{id} - Suppression réussie")
    void testDeleteMenu_Success() throws Exception {
        doNothing().when(menuService).delete(1L);

        mockMvc.perform(delete("/api/menus/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/menus/{id} - Échec si contient catégories (400)")
    void testDeleteMenu_WithCategories() throws Exception {
        doThrow(new RuntimeException("Impossible de supprimer un menu contenant des catégories"))
                .when(menuService).delete(1L);

        mockMvc.perform(delete("/api/menus/1"))
                .andExpect(status().isBadRequest());
    }
}
