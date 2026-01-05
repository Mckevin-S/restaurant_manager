package com.example.BackendProject.TestController;


import com.example.BackendProject.controllers.CuisineController;
import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.services.implementations.CuisineServiceImplementation;
import com.example.BackendProject.utils.StatutCommande;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - CuisineController")
class CuisineControllerTest {

    @Mock
    private CuisineServiceImplementation cuisineService;

    @InjectMocks
    private CuisineController cuisineController;

    private MockMvc mockMvc;
    private CommandeDto commandeDto;

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
        mockMvc = MockMvcBuilders.standaloneSetup(cuisineController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        commandeDto = new CommandeDto();
        commandeDto.setId(1L);
        commandeDto.setStatut(StatutCommande.valueOf("EN_PREPARATION"));
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/cuisine/en-cours - Récupérer les commandes à préparer")
    void testGetCommandesCuisine_Success() throws Exception {
        when(cuisineService.getListeAPreparer()).thenReturn(Arrays.asList(commandeDto));

        mockMvc.perform(get("/api/cuisine/en-cours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ==================== Tests STATUS UPDATES ====================

    @Test
    @DisplayName("PATCH /api/cuisine/{id}/commencer - Commencer une préparation")
    void testCommencerPreparation_Success() throws Exception {
        when(cuisineService.commencerPreparation(1L)).thenReturn(commandeDto);

        mockMvc.perform(patch("/api/cuisine/1/commencer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("EN_PREPARATION"));
    }

    @Test
    @DisplayName("PATCH /api/cuisine/{id}/commencer - Commande inexistante (404)")
    void testCommencerPreparation_NotFound() throws Exception {
        when(cuisineService.commencerPreparation(999L))
                .thenThrow(new RuntimeException("Commande non trouvée avec l'ID : 999"));

        mockMvc.perform(patch("/api/cuisine/999/commencer"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Commande non trouvée avec l'ID : 999"));
    }

    @Test
    @DisplayName("PATCH /api/cuisine/{id}/terminer - Terminer une préparation")
    void testTerminerPreparation_Success() throws Exception {
        commandeDto.setStatut(StatutCommande.valueOf("PRETE"));
        when(cuisineService.marquerCommePrete(1L)).thenReturn(commandeDto);

        mockMvc.perform(patch("/api/cuisine/1/terminer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("PRETE"));
    }

    @Test
    @DisplayName("PATCH /api/cuisine/{id}/terminer - Échec si déjà terminée (400)")
    void testTerminerPreparation_BadRequest() throws Exception {
        when(cuisineService.marquerCommePrete(1L))
                .thenThrow(new RuntimeException("La commande n'est pas en cours de préparation"));

        mockMvc.perform(patch("/api/cuisine/1/terminer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La commande n'est pas en cours de préparation"));
    }
}
