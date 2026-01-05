package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.RecetteController;
import com.example.BackendProject.dto.RecetteDto;
import com.example.BackendProject.services.implementations.RecetteServiceImplementation;
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
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - RecetteController")
class RecetteControllerTest {

    @Mock
    private RecetteServiceImplementation recetteService;

    @InjectMocks
    private RecetteController recetteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RecetteDto recetteDto;

    /**
     * Gestionnaire d'exceptions pour transformer les RuntimeException en 404/400
     */
    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
            HttpStatus status = e.getMessage().toLowerCase().contains("non trouvé") ||
                    e.getMessage().toLowerCase().contains("introuvable")
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recetteController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // Initialisation avec vos champs réels
        recetteDto = new RecetteDto();
        recetteDto.setId(1L);
        recetteDto.setPlat(10L); // ID du plat associé
        recetteDto.setNom("Recette du Burger Classic");
    }

    @Test
    @DisplayName("POST /api/recettes - Créer une recette")
    void testCreate_Success() throws Exception {
        when(recetteService.save(any(RecetteDto.class))).thenReturn(recetteDto);

        mockMvc.perform(post("/api/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recetteDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Recette du Burger Classic"))
                .andExpect(jsonPath("$.plat").value(10));
    }

    @Test
    @DisplayName("GET /api/recettes/{id} - Récupérer par ID")
    void testGetById_Success() throws Exception {
        when(recetteService.getById(1L)).thenReturn(recetteDto);

        mockMvc.perform(get("/api/recettes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Recette du Burger Classic"));
    }

    @Test
    @DisplayName("GET /api/recettes/plat/{platId} - Lister par Plat")
    void testGetByPlat_Success() throws Exception {
        when(recetteService.getByPlat(10L)).thenReturn(Arrays.asList(recetteDto));

        mockMvc.perform(get("/api/recettes/plat/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].plat").value(10));
    }

    @Test
    @DisplayName("DELETE /api/recettes/{id} - Suppression réussie")
    void testDelete_Success() throws Exception {
        doNothing().when(recetteService).delete(1L);

        mockMvc.perform(delete("/api/recettes/1"))
                .andExpect(status().isOk());
    }
}