package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.services.implementations.PlatServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour le contrôleur PlatController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests d'Intégration du Controller Plat")
class PlatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlatServiceImplementation platService;

    private PlatDto platDto;

    @BeforeEach
    void setUp() {
        platDto = new PlatDto();
        platDto.setId(1L);
        platDto.setNom("Salade César");
        platDto.setDescription("Salade fraîche avec poulet grillé");
        platDto.setPrix(BigDecimal.valueOf(12.50));
        platDto.setDisponibilite(true);
        platDto.setCategory(1L);
    }

    @Test
    @DisplayName("GET /api/plats - Devrait retourner tous les plats")
    void devraitRetournerTousLesPlats() throws Exception {
        // Arrange
        List<PlatDto> plats = Arrays.asList(platDto);
        when(platService.getAll()).thenReturn(plats);

        // Act & Assert
        mockMvc.perform(get("/api/plats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nom").value("Salade César"))
                .andExpect(jsonPath("$[0].prix").value(12.50));
    }

    @Test
    @DisplayName("GET /api/plats/{id} - Devrait retourner un plat par ID")
    void devraitRetournerPlatParId() throws Exception {
        // Arrange
        when(platService.getById(1L)).thenReturn(platDto);

        // Act & Assert
        mockMvc.perform(get("/api/plats/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Salade César"));
    }

    @Test
    @DisplayName("POST /api/plats - Devrait créer un nouveau plat avec des données valides")
    @WithMockUser(roles = "MANAGER")
    void devraitCreerNouveauPlat() throws Exception {
        // Arrange
        when(platService.save(any(PlatDto.class))).thenReturn(platDto);

        // Act & Assert
        mockMvc.perform(post("/api/plats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(platDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Salade César"));
    }

    @Test
    @DisplayName("POST /api/plats - Devrait rejeter un plat avec des données invalides")
    @WithMockUser(roles = "MANAGER")
    void devraitRejeterPlatInvalide() throws Exception {
        // Arrange - Plat sans nom (invalide)
        PlatDto platInvalide = new PlatDto();
        platInvalide.setPrix(BigDecimal.valueOf(-5.00)); // Prix négatif
        platInvalide.setCategory(1L);

        // Act & Assert
        mockMvc.perform(post("/api/plats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(platInvalide)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/plats/{id} - Devrait mettre à jour un plat existant")
    @WithMockUser(roles = "MANAGER")
    void devraitMettreAJourPlat() throws Exception {
        // Arrange
        PlatDto platMisAJour = new PlatDto();
        platMisAJour.setId(1L);
        platMisAJour.setNom("Salade César Royale");
        platMisAJour.setPrix(BigDecimal.valueOf(15.00));
        platMisAJour.setCategory(1L);

        when(platService.update(anyLong(), any(PlatDto.class))).thenReturn(platMisAJour);

        // Act & Assert
        mockMvc.perform(put("/api/plats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(platMisAJour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Salade César Royale"))
                .andExpect(jsonPath("$.prix").value(15.00));
    }

    @Test
    @DisplayName("DELETE /api/plats/{id} - Devrait supprimer un plat")
    @WithMockUser(roles = "MANAGER")
    void devraitSupprimerPlat() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/plats/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/plats/disponibles - Devrait retourner uniquement les plats disponibles")
    void devraitRetournerPlatsDisponibles() throws Exception {
        // Arrange
        when(platService.getMenuActif()).thenReturn(Arrays.asList(platDto));

        // Act & Assert
        mockMvc.perform(get("/api/plats/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponibilite").value(true));
    }
}
