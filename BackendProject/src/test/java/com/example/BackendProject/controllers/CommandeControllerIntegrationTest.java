package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.services.implementations.CommandeServiceImplementation;
import com.example.BackendProject.utils.StatutCommande;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests d'Intégration du Controller Commande")
class CommandeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommandeServiceImplementation commandeService;

    private CommandeDto commandeDto;

    @BeforeEach
    void setUp() {
        commandeDto = new CommandeDto();
        commandeDto.setId(1L);
        commandeDto.setTableId(1L);
        commandeDto.setServeurId(1L);
        commandeDto.setDateHeureCommande(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        commandeDto.setStatut(StatutCommande.EN_ATTENTE);
        commandeDto.setTotalHt(BigDecimal.valueOf(10000));
        commandeDto.setTotalTtc(BigDecimal.valueOf(11800));
    }

    @Test
    @DisplayName("GET /api/commandes - Devrait retourner toutes les commandes")
    @WithMockUser(roles = "MANAGER")
    void devraitRetournerToutesLesCommandes() throws Exception {
        List<CommandeDto> commandes = Arrays.asList(commandeDto);
        when(commandeService.getAll()).thenReturn(commandes);

        mockMvc.perform(get("/api/commandes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"))
                .andExpect(jsonPath("$[0].totalTtc").value(11800));
    }

    @Test
    @DisplayName("GET /api/commandes/{id} - Devrait retourner une commande par ID")
    @WithMockUser(roles = "SERVEUR")
    void devraitRetournerCommandeParId() throws Exception {
        when(commandeService.getById(1L)).thenReturn(commandeDto);

        mockMvc.perform(get("/api/commandes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    @DisplayName("POST /api/commandes - Devrait créer une nouvelle commande")
    @WithMockUser(roles = "SERVEUR")
    void devraitCreerNouvelleCommande() throws Exception {
        when(commandeService.save(any(CommandeDto.class))).thenReturn(commandeDto);

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    @DisplayName("POST /api/commandes - Devrait rejeter une commande avec données invalides")
    @WithMockUser(roles = "SERVEUR")
    void devraitRejeterCommandeInvalide() throws Exception {
        CommandeDto commandeInvalide = new CommandeDto();
        // Pas de tableId ni serveurId (invalide)

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeInvalide)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/commandes/{id}/statut - Devrait changer le statut")
    @WithMockUser(roles = "CUISINIER")
    void devraitChangerStatutCommande() throws Exception {
        commandeDto.setStatut(StatutCommande.EN_PREPARATION);
        when(commandeService.updateStatut(anyLong(), any(StatutCommande.class)))
                .thenReturn(commandeDto);

        mockMvc.perform(patch("/api/commandes/1/statut")
                        .param("statut", "EN_PREPARATION")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\": \"EN_PREPARATION\"}")) // Patch expect body usually now
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_PREPARATION"));
    }

    @Test
    @DisplayName("GET /api/commandes/table/{tableId} - Devrait retourner les commandes d'une table")
    @WithMockUser(roles = "SERVEUR")
    void devraitRetournerCommandesParTable() throws Exception {
        when(commandeService.findByTable(1L)).thenReturn(Arrays.asList(commandeDto));

        mockMvc.perform(get("/api/commandes/table/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableId").value(1));
    }

    @Test
    @DisplayName("GET /api/commandes/serveur/{serveurId} - Devrait retourner les commandes d'un serveur")
    @WithMockUser(roles = "MANAGER")
    void devraitRetournerCommandesParServeur() throws Exception {
        when(commandeService.findByServeur(1L)).thenReturn(Arrays.asList(commandeDto));

        mockMvc.perform(get("/api/commandes/serveur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serveurId").value(1));
    }

    @Test
    @DisplayName("GET /api/commandes/statut/{statut} - Devrait retourner les commandes par statut")
    @WithMockUser(roles = "CUISINIER")
    void devraitRetournerCommandesParStatut() throws Exception {
        when(commandeService.findByStatut(StatutCommande.EN_ATTENTE))
                .thenReturn(Arrays.asList(commandeDto));

        mockMvc.perform(get("/api/commandes/statut/EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"));
    }

    @Test
    @DisplayName("DELETE /api/commandes/{id} - Devrait supprimer une commande")
    @WithMockUser(roles = "MANAGER")
    void devraitSupprimerCommande() throws Exception {
        mockMvc.perform(delete("/api/commandes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/commandes - Sans authentification devrait retourner 401")
    void devraitRetourner401SansAuthentification() throws Exception {
        mockMvc.perform(get("/api/commandes"))
                .andExpect(status().isUnauthorized());
    }
}
