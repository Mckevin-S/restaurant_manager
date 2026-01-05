package com.example.BackendProject.TestController;


import com.example.BackendProject.controllers.CommandeController;
import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.services.implementations.CommandeServiceImplementation;
import com.example.BackendProject.services.implementations.UtilisateurServiceImplementation;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import com.example.BackendProject.utils.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - CommandeController")
class CommandeControllerTest {

    @Mock
    private CommandeServiceImplementation commandeService;

    @Mock
    private UtilisateurServiceImplementation utilisateurService;

    @InjectMocks
    private CommandeController commandeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CommandeDto commandeDto;

    /**
     * Gestionnaire d'exceptions interne pour mapper les erreurs de service vers les statuts HTTP
     */
    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<?> handleRuntime(RuntimeException e) {
            if (e.getMessage().contains("non trouvée") || e.getMessage().contains("non trouvé")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commandeController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Pour gérer LocalDateTime

        commandeDto = new CommandeDto();
        commandeDto.setId(1L);
        commandeDto.setTotalTtc(new BigDecimal("45.50"));;
        commandeDto.setStatut(StatutCommande.EN_ATTENTE);
        commandeDto.setTypeCommande(TypeCommande.SUR_PLACE);
    }

    // ==================== Tests CREATE ====================

    @Test
    @DisplayName("POST /api/commandes - Créer une commande avec succès")
    void testCreateCommande_Success() throws Exception {
        when(commandeService.save(any(CommandeDto.class))).thenReturn(commandeDto);

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));

        verify(commandeService, times(1)).save(any(CommandeDto.class));
    }

    @Test
    @DisplayName("POST /api/commandes - Échec (Données invalides)")
    void testCreateCommande_Error() throws Exception {
        when(commandeService.save(any(CommandeDto.class)))
                .thenThrow(new RuntimeException("Erreur de validation des données"));

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erreur de validation des données"));
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/commandes/{id} - Succès")
    void testGetCommandeById_Success() throws Exception {
        when(commandeService.getById(1L)).thenReturn(commandeDto);

        mockMvc.perform(get("/api/commandes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/commandes/{id} - Non trouvé")
    void testGetCommandeById_NotFound() throws Exception {
        when(commandeService.getById(99L)).thenThrow(new RuntimeException("Commande non trouvée"));

        mockMvc.perform(get("/api/commandes/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== Tests DELETE ====================

    @Test
    @DisplayName("DELETE /api/commandes/{id} - Succès")
    void testDeleteCommande_Success() throws Exception {
        doNothing().when(commandeService).delete(1L);

        mockMvc.perform(delete("/api/commandes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/commandes/{id} - Échec car payée")
    void testDeleteCommande_Payee() throws Exception {
        doThrow(new RuntimeException("Impossible de supprimer une commande payée"))
                .when(commandeService).delete(1L);

        mockMvc.perform(delete("/api/commandes/1"))
                .andExpect(status().isBadRequest());
    }

    // ==================== Tests STATUT & FILTRES ====================

    @Test
    @DisplayName("PATCH /api/commandes/{id}/statut - Mise à jour statut")
    void testUpdateStatut_Success() throws Exception {
        commandeDto.setStatut(StatutCommande.PRETE);
        when(commandeService.updateStatut(eq(1L), any(StatutCommande.class))).thenReturn(commandeDto);

        mockMvc.perform(patch("/api/commandes/1/statut")
                        .param("statut", "PRETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("PRETE"));
    }

    @Test
    @DisplayName("GET /api/commandes/statut/{statut} - Filtre par statut")
    void testGetByStatut() throws Exception {
        when(commandeService.findByStatut(StatutCommande.EN_ATTENTE))
                .thenReturn(Arrays.asList(commandeDto));

        mockMvc.perform(get("/api/commandes/statut/EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ==================== Tests STATISTIQUES ====================

    @Test
    @DisplayName("GET /api/commandes/statistiques/ventes - Calcul CA")
    void testGetTotalVentes() throws Exception {
        when(commandeService.calculateTotalVentes(any(), any()))
                .thenReturn(new BigDecimal("1500.00"));

        mockMvc.perform(get("/api/commandes/statistiques/ventes")
                        .param("debut", "2023-01-01T00:00:00")
                        .param("fin", "2023-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVentes").value(1500.00));
    }

    @Test
    @DisplayName("GET /api/commandes/statistiques/en-cours - Compteur")
    void testCountEnCours() throws Exception {
        when(commandeService.countCommandesEnCours()).thenReturn(5L);

        mockMvc.perform(get("/api/commandes/statistiques/en-cours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commandesEnCours").value(5));
    }

    // ==================== Tests SERVEURS ====================

    @Test
    @DisplayName("GET /api/commandes/serveurs - Liste des serveurs")
    void testGetAllServeurs() throws Exception {
        UtilisateurDto serveur = new UtilisateurDto();
        serveur.setNom("Jean");

        when(utilisateurService.findByRoleType(RoleType.SERVEUR))
                .thenReturn(Arrays.asList(serveur));

        mockMvc.perform(get("/api/commandes/serveurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Jean"));
    }
}
