package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.PaiementController;
import com.example.BackendProject.dto.PaiementDto;
import com.example.BackendProject.services.implementations.PaiementServiceImplementation;
import com.example.BackendProject.utils.TypePaiement;
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
import java.sql.Timestamp;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - PaiementController")
class PaiementControllerTest {

    @Mock
    private PaiementServiceImplementation paiementService;

    @InjectMocks
    private PaiementController paiementController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PaiementDto paiementDto;

    /**
     * Gestionnaire d'exceptions local pour simuler le buildErrorResponse du contrôleur
     */
    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
            HttpStatus status = (e.getMessage().toLowerCase().contains("non trouvé") ||
                    e.getMessage().toLowerCase().contains("non trouvée"))
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paiementController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        paiementDto = new PaiementDto();
        paiementDto.setId(1L);
        paiementDto.setCommande(100L);
        paiementDto.setMontant(new BigDecimal("50.00"));
        paiementDto.setTypePaiement(TypePaiement.Carte);
        paiementDto.setReferenceTransaction("REF-123");
    }

    // ==================== Tests CREATE & EFFECTUER ====================

    @Test
    @DisplayName("POST /api/paiements - Créer un paiement avec succès")
    void testCreatePaiement_Success() throws Exception {
        when(paiementService.save(any(PaiementDto.class))).thenReturn(paiementDto);

        mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paiementDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.referenceTransaction").value("REF-123"));
    }

//    @Test
//    @DisplayName("POST /api/paiements/effectuer - Effectuer paiement simplifié")
//    void testEffectuerPaiement_Success() throws Exception {
//        // 1. Utilisez l'Enum exact (souvent en majuscules) et assurez-vous qu'il matche le paramètre
//        when(paiementService.effectuerPaiement(eq(100L), any(BigDecimal.class), eq(TypePaiement.Carte)))
//                .thenReturn(paiementDto);
//
//        mockMvc.perform(post("/api/paiements/effectuer")
//                        .param("commandeId", "100")
//                        .param("montant", "50.00")
//                        .param("typePaiement", "CARTE")) // Doit être identique à l'Enum du "when"
//                .andExpect(status().isCreated())
//                // 2. Vérifiez si c'est "$.commande" ou "$.commandeId" selon votre DTO
//                .andExpect(jsonPath("$.commande").value(100));
//    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/paiements/{id} - Paiement non trouvé (404)")
    void testGetPaiementById_NotFound() throws Exception {
        when(paiementService.getById(999L)).thenThrow(new RuntimeException("Paiement non trouvé"));

        mockMvc.perform(get("/api/paiements/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("non trouvé")));
    }

    @Test
    @DisplayName("GET /api/paiements/reference/{ref} - Trouver par référence")
    void testGetPaiementByReference_Success() throws Exception {
        when(paiementService.findByReference("REF-123")).thenReturn(paiementDto);

        mockMvc.perform(get("/api/paiements/reference/REF-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceTransaction").value("REF-123"));
    }

    // ==================== Tests STATISTIQUES ====================

    @Test
    @DisplayName("GET /api/paiements/statistiques/total - Calculer total période")
    void testCalculateTotal_Success() throws Exception {
        BigDecimal total = new BigDecimal("500.00");
        when(paiementService.calculateTotalByPeriod(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(total);

        mockMvc.perform(get("/api/paiements/statistiques/total")
                        .param("debut", "2023-01-01T00:00:00")
                        .param("fin", "2023-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(500.00));
    }

    @Test
    @DisplayName("GET /api/paiements/commande/{id}/statut-paiement - Vérifier si payée")
    void testIsCommandePayee() throws Exception {
        when(paiementService.commandeEstPayee(100L)).thenReturn(true);

        mockMvc.perform(get("/api/paiements/commande/100/statut-paiement"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // ==================== Tests DELETE ====================

    @Test
    @DisplayName("DELETE /api/paiements/{id} - Supprimer avec succès")
    void testDeletePaiement_Success() throws Exception {
        doNothing().when(paiementService).delete(1L);

        mockMvc.perform(delete("/api/paiements/1"))
                .andExpect(status().isNoContent());
    }
}
