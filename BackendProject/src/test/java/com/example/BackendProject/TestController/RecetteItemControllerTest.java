package com.example.BackendProject.TestController;


import com.example.BackendProject.controllers.RecetteItemController;
import com.example.BackendProject.dto.RecetteItemDto;
import com.example.BackendProject.services.implementations.RecetteItemServiceImplementation;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - RecetteItemController")
class RecetteItemControllerTest {

    @Mock
    private RecetteItemServiceImplementation recetteItemService;

    @InjectMocks
    private RecetteItemController recetteItemController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RecetteItemDto itemDto;

    /**
     * Gestionnaire d'exceptions pour simuler les réponses 404/400
     */
    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
            HttpStatus status = e.getMessage().toLowerCase().contains("non trouvé")
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recetteItemController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // Initialisation avec vos champs réels
        itemDto = new RecetteItemDto();
        itemDto.setId(1L);
        itemDto.setRecette(100L);      // ID de la Recette
        itemDto.setIngredient(50L);   // ID de l'Ingrédient
        itemDto.setQuantiteRequise(new BigDecimal("2.50"));
    }

    @Test
    @DisplayName("POST /api/recette-items - Ajouter un ingrédient")
    void testCreate_Success() throws Exception {
        when(recetteItemService.save(any(RecetteItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/api/recette-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.recette").value(100))
                .andExpect(jsonPath("$.ingredient").value(50))
                .andExpect(jsonPath("$.quantiteRequise").value(2.50));
    }

    @Test
    @DisplayName("GET /api/recette-items/recette/{id} - Lister par recette")
    void testGetByRecette_Success() throws Exception {
        when(recetteItemService.getByRecette(100L)).thenReturn(Arrays.asList(itemDto));

        mockMvc.perform(get("/api/recette-items/recette/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantiteRequise").value(2.50));
    }

    @Test
    @DisplayName("GET /api/recette-items/{id} - Item non trouvé")
    void testGetById_NotFound() throws Exception {
        when(recetteItemService.getById(999L)).thenThrow(new RuntimeException("Item non trouvé"));

        mockMvc.perform(get("/api/recette-items/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item non trouvé"));
    }

    @Test
    @DisplayName("DELETE /api/recette-items/{id} - Suppression réussie")
    void testDelete_Success() throws Exception {
        doNothing().when(recetteItemService).delete(1L);

        mockMvc.perform(delete("/api/recette-items/1"))
                .andExpect(status().isOk());
    }
}
