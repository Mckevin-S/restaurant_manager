package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.OptionItemController;
import com.example.BackendProject.dto.OptionItemDto;
import com.example.BackendProject.services.implementations.OptionItemServiceImplementation;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - OptionItemController")
class OptionItemControllerTest {

    @Mock
    private OptionItemServiceImplementation optionService;

    @InjectMocks
    private OptionItemController optionItemController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OptionItemDto optionDto;

    /**
     * Gestionnaire d'exceptions local pour simuler le comportement du BackendProject
     */
    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(optionItemController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        optionDto = new OptionItemDto();
        optionDto.setId(1L);
        optionDto.setNom("Supplément Fromage");
        optionDto.setPrixSupplementaire(new BigDecimal("1.50"));
    }

    // ==================== Tests CREATE ====================

    @Test
    @DisplayName("POST /api/options - Créer une option avec succès")
    void testCreateOption_Success() throws Exception {
        when(optionService.save(any(OptionItemDto.class))).thenReturn(optionDto);

        mockMvc.perform(post("/api/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(optionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Supplément Fromage"));
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/options - Récupérer toutes les options")
    void testGetAllOptions_Success() throws Exception {
        when(optionService.getAll()).thenReturn(Arrays.asList(optionDto));

        mockMvc.perform(get("/api/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom").value("Supplément Fromage"));
    }

    @Test
    @DisplayName("GET /api/options/{id} - Récupérer par ID")
    void testGetOptionById_Success() throws Exception {
        when(optionService.getById(1L)).thenReturn(optionDto);

        mockMvc.perform(get("/api/options/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/options/{id} - Option non trouvée (404)")
    void testGetOptionById_NotFound() throws Exception {
        when(optionService.getById(999L))
                .thenThrow(new RuntimeException("Option non trouvée avec l'ID : 999"));

        mockMvc.perform(get("/api/options/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Option non trouvée avec l'ID : 999"));
    }

    // ==================== Tests UPDATE & DELETE ====================

    @Test
    @DisplayName("PUT /api/options/{id} - Mettre à jour une option")
    void testUpdateOption_Success() throws Exception {
        when(optionService.update(eq(1L), any(OptionItemDto.class))).thenReturn(optionDto);

        mockMvc.perform(put("/api/options/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(optionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Supplément Fromage"));
    }

    @Test
    @DisplayName("DELETE /api/options/{id} - Supprimer une option")
    void testDeleteOption_Success() throws Exception {
        doNothing().when(optionService).delete(1L);

        mockMvc.perform(delete("/api/options/1"))
                .andExpect(status().isNoContent());
    }
}
