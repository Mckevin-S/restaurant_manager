package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.PromotionController;
import com.example.BackendProject.dto.PromotionDto;
import com.example.BackendProject.services.implementations.PromotionServiceImplementation;
import com.example.BackendProject.utils.TypePromotion; // Vérifiez l'import de votre Enum
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - PromotionController")
class PromotionControllerTest {

    @Mock
    private PromotionServiceImplementation promotionService;

    @InjectMocks
    private PromotionController promotionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PromotionDto promotionDto;

    /**
     * Gestionnaire d'exceptions local pour capturer les erreurs métiers
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
        mockMvc = MockMvcBuilders.standaloneSetup(promotionController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        promotionDto = new PromotionDto();
        promotionDto.setId(1L);
        promotionDto.setNom("PROMO_FLASH");
        promotionDto.setType(TypePromotion.Pourcentage); // Ajustez selon vos valeurs d'Enum
        promotionDto.setValeur(new BigDecimal("25.00"));
        promotionDto.setActif(true);
        // Convertit l'instant présent en java.sql.Date
        promotionDto.setDateExpiration(new java.sql.Date(System.currentTimeMillis() + 86400000));
    }

    // ==================== Tests de Création ====================

    @Test
    @DisplayName("POST /api/promotions - Créer une promotion avec succès")
    void testCreate_Success() throws Exception {
        when(promotionService.save(any(PromotionDto.class))).thenReturn(promotionDto);

        mockMvc.perform(post("/api/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promotionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("PROMO_FLASH"))
                .andExpect(jsonPath("$.valeur").value(25.00));
    }

    // ==================== Tests de Lecture ====================

    @Test
    @DisplayName("GET /api/promotions - Liste toutes les promotions")
    void testGetAll_Success() throws Exception {
        when(promotionService.findAll()).thenReturn(Arrays.asList(promotionDto));

        mockMvc.perform(get("/api/promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom").value("PROMO_FLASH"));
    }

    @Test
    @DisplayName("GET /api/promotions/{id} - Promotion trouvée")
    void testGetOne_Success() throws Exception {
        when(promotionService.findById(1L)).thenReturn(promotionDto);

        mockMvc.perform(get("/api/promotions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.actif").value(true));
    }

    @Test
    @DisplayName("GET /api/promotions/{id} - 404 si inexistante")
    void testGetOne_NotFound() throws Exception {
        when(promotionService.findById(99L)).thenThrow(new RuntimeException("Promotion non trouvée"));

        mockMvc.perform(get("/api/promotions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("non trouvée")));
    }

    // ==================== Tests de Modification ====================

    @Test
    @DisplayName("PUT /api/promotions/{id} - Mise à jour réussie")
    void testUpdate_Success() throws Exception {
        when(promotionService.update(eq(1L), any(PromotionDto.class))).thenReturn(promotionDto);

        mockMvc.perform(put("/api/promotions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promotionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valeur").value(25.00));
    }

    // ==================== Tests de Suppression ====================

    @Test
    @DisplayName("DELETE /api/promotions/{id} - Suppression réussie")
    void testDelete_Success() throws Exception {
        doNothing().when(promotionService).delete(1L);

        mockMvc.perform(delete("/api/promotions/1"))
                .andExpect(status().isNoContent());
    }
}