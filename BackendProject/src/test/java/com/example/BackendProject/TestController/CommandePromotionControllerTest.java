package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.CommandePromotionController;
import com.example.BackendProject.dto.CommandePromotionDto;
import com.example.BackendProject.services.implementations.CommandePromotionServiceImplementation;
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
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - CommandePromotionController")
class CommandePromotionControllerTest {

    @Mock
    private CommandePromotionServiceImplementation commandePromotionService;

    @InjectMocks
    private CommandePromotionController commandePromotionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CommandePromotionDto cpDto;

    /**
     * Handler local pour simuler la logique métier des blocs try-catch du contrôleur
     */
    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException e) {
            HttpStatus status = e.getMessage().contains("non trouvée") ?
                    HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commandePromotionController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        cpDto = new CommandePromotionDto();
        cpDto.setCommande(1L);
        cpDto.setPromotion(10L);
    }

    // ==================== Tests CREATE / APPLIQUER ====================

    @Test
    @DisplayName("POST /api/commande-promotions - Créer une association avec succès")
    void testCreate_Success() throws Exception {
        when(commandePromotionService.save(any(CommandePromotionDto.class))).thenReturn(cpDto);

        mockMvc.perform(post("/api/commande-promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cpDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commande").value(1))
                .andExpect(jsonPath("$.promotion").value(10));
    }

    @Test
    @DisplayName("POST /.../appliquer/{id} - Succès méthode simplifiée")
    void testAppliquerPromotion_Success() throws Exception {
        when(commandePromotionService.appliquerPromotion(1L, 10L)).thenReturn(cpDto);

        mockMvc.perform(post("/api/commande-promotions/commande/1/appliquer/10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.promotion").value(10));
    }

    @Test
    @DisplayName("POST /.../appliquer/{id} - Échec car expirée (400)")
    void testAppliquerPromotion_Expired() throws Exception {
        when(commandePromotionService.appliquerPromotion(1L, 10L))
                .thenThrow(new RuntimeException("Cette promotion est expirée"));

        mockMvc.perform(post("/api/commande-promotions/commande/1/appliquer/10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cette promotion est expirée"));
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/commande-promotions - Liste complète")
    void testGetAll_Success() throws Exception {
        when(commandePromotionService.getAll()).thenReturn(Arrays.asList(cpDto));

        mockMvc.perform(get("/api/commande-promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/commande-promotions/commande/{id} - Par commande")
    void testGetByCommande_Success() throws Exception {
        when(commandePromotionService.findByCommandeId(1L)).thenReturn(Arrays.asList(cpDto));

        mockMvc.perform(get("/api/commande-promotions/commande/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].promotion").value(10));
    }

    @Test
    @DisplayName("GET /.../verifier - Vérifier présence promotion")
    void testVerifierPromotion() throws Exception {
        when(commandePromotionService.promotionEstAppliquee(1L, 10L)).thenReturn(true);

        mockMvc.perform(get("/api/commande-promotions/commande/1/promotion/10/verifier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promotionAppliquee").value(true));
    }

    // ==================== Tests DELETE / RETIRER ====================

    @Test
    @DisplayName("DELETE /.../retirer/{id} - Retirer une promotion avec succès")
    void testRetirerPromotion_Success() throws Exception {
        doNothing().when(commandePromotionService).retirerPromotion(1L, 10L);

        mockMvc.perform(delete("/api/commande-promotions/commande/1/retirer/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /.../retirer-toutes - Retirer tout avec succès")
    void testRetirerToutes_Success() throws Exception {
        doNothing().when(commandePromotionService).retirerToutesPromotions(1L);

        mockMvc.perform(delete("/api/commande-promotions/commande/1/retirer-toutes"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /.../retirer/{id} - Échec (404)")
    void testRetirerPromotion_NotFound() throws Exception {
        doThrow(new RuntimeException("Association non trouvée"))
                .when(commandePromotionService).retirerPromotion(1L, 99L);

        mockMvc.perform(delete("/api/commande-promotions/commande/1/retirer/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Association non trouvée"));
    }
}
