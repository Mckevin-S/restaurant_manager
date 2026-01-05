package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.LigneCommandeController;
import com.example.BackendProject.dto.LigneCommandeDto;
import com.example.BackendProject.services.implementations.LigneCommandeServiceImplementation;
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
@DisplayName("Tests unitaires - LigneCommandeController")
class LigneCommandeControllerTest {

    @Mock
    private LigneCommandeServiceImplementation ligneCommandeService;

    @InjectMocks
    private LigneCommandeController ligneCommandeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LigneCommandeDto ligneDto;

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
        mockMvc = MockMvcBuilders.standaloneSetup(ligneCommandeController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        ligneDto = new LigneCommandeDto();
        ligneDto.setId(1L);
        ligneDto.setCommande(10L);
        ligneDto.setPlat(5L);
        ligneDto.setQuantite(2);
        ligneDto.setPrixUnitaire(new BigDecimal("15.50"));
    }

    // ==================== Tests CREATE ====================

    @Test
    @DisplayName("POST /api/ligne-commandes - Créer une ligne avec succès")
    void testCreateLigneCommande_Success() throws Exception {
        when(ligneCommandeService.save(any(LigneCommandeDto.class))).thenReturn(ligneDto);

        mockMvc.perform(post("/api/ligne-commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ligneDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/ligne-commandes/commande/{id}/ajouter - Ajouter article simplifié")
    void testAjouterArticle_Success() throws Exception {
        when(ligneCommandeService.ajouterLigneCommande(eq(10L), eq(5L), eq(2), anyString()))
                .thenReturn(ligneDto);

        mockMvc.perform(post("/api/ligne-commandes/commande/10/ajouter")
                        .param("platId", "5")
                        .param("quantite", "2")
                        .param("notes", "Mes notes"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commande").value(10)); // Vérifiez le nom du champ (commandeId ou commande)
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/ligne-commandes/commande/{id} - Liste des articles d'une commande")
    void testGetLignesByCommande_Success() throws Exception {
        when(ligneCommandeService.findByCommandeId(10L)).thenReturn(Arrays.asList(ligneDto));

        mockMvc.perform(get("/api/ligne-commandes/commande/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].plat").value(5));
    }

    @Test
    @DisplayName("GET /api/ligne-commandes/{id} - Ligne non trouvée (404)")
    void testGetLigneById_NotFound() throws Exception {
        when(ligneCommandeService.getById(99L))
                .thenThrow(new RuntimeException("Ligne de commande non trouvée avec l'ID : 99"));

        mockMvc.perform(get("/api/ligne-commandes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("non trouvée")));
    }

    // ==================== Tests UPDATE & DELETE ====================

    @Test
    @DisplayName("PATCH /api/ligne-commandes/{id}/quantite - Modifier quantité")
    void testUpdateQuantite_Success() throws Exception {
        ligneDto.setQuantite(5);
        when(ligneCommandeService.updateQuantite(eq(1L), eq(5))).thenReturn(ligneDto);

        mockMvc.perform(patch("/api/ligne-commandes/1/quantite")
                        .param("quantite", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantite").value(5));
    }

    @Test
    @DisplayName("DELETE /api/ligne-commandes/commande/{id}/vider - Vider une commande")
    void testViderCommande_Success() throws Exception {
        doNothing().when(ligneCommandeService).supprimerToutesLignesCommande(10L);

        mockMvc.perform(delete("/api/ligne-commandes/commande/10/vider"))
                .andExpect(status().isNoContent());
    }

    // ==================== Tests CALCULS ====================

    @Test
    @DisplayName("GET /api/ligne-commandes/commande/{id}/total - Calculer le total")
    void testCalculateTotal_Success() throws Exception {
        BigDecimal total = new BigDecimal("31.00");
        when(ligneCommandeService.calculateTotalCommande(10L)).thenReturn(total);

        mockMvc.perform(get("/api/ligne-commandes/commande/10/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(31.00))
                .andExpect(jsonPath("$.commandeId").value(10));
    }
}
