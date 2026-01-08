package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.IngredientController;
import com.example.BackendProject.dto.IngredientDto;
import com.example.BackendProject.services.implementations.IngredientServiceImplementation;
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
import static org.hamcrest.Matchers.containsString;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - IngredientController")
class IngredientControllerTest {

    @Mock
    private IngredientServiceImplementation ingredientService;

    @InjectMocks
    private IngredientController ingredientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private IngredientDto ingredientDto;

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
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        ingredientDto = new IngredientDto();
        ingredientDto.setId(1L);
        ingredientDto.setNom("Sel");
        ingredientDto.setQuantiteActuelle(new BigDecimal("10.0"));
        ingredientDto.setUniteMesure("kg");
        ingredientDto.setSeuilAlerte(new BigDecimal("2.0"));
    }

    // ==================== Tests CREATE ====================

    @Test
    @DisplayName("POST /api/ingredients - Créer un ingrédient avec succès")
    void testCreateIngredient_Success() throws Exception {
        when(ingredientService.save(any(IngredientDto.class))).thenReturn(ingredientDto);

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Sel"));
    }

    @Test
    @DisplayName("POST /api/ingredients - Échec si le nom existe déjà (400)")
    void testCreateIngredient_AlreadyExists() throws Exception {
        when(ingredientService.save(any(IngredientDto.class)))
                .thenThrow(new RuntimeException("Un ingrédient avec ce nom existe déjà"));

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Un ingrédient avec ce nom existe déjà"));
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/ingredients - Récupérer tous les ingrédients")
    void testGetAllIngredients_Success() throws Exception {
        when(ingredientService.getAll()).thenReturn(Arrays.asList(ingredientDto));

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom").value("Sel"));
    }

    @Test
    @DisplayName("GET /api/ingredients/{id} - Ingrédient non trouvé (404)")
    void testGetIngredientById_NotFound() throws Exception {
        when(ingredientService.getById(999L))
                .thenThrow(new RuntimeException("Ingrédient non trouvé avec l'ID : 999"));

        mockMvc.perform(get("/api/ingredients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("non trouvé")));
    }

    // ==================== Tests UPDATE & DELETE ====================

    @Test
    @DisplayName("PUT /api/ingredients/{id} - Mettre à jour avec succès")
    void testUpdateIngredient_Success() throws Exception {
        when(ingredientService.update(eq(1L), any(IngredientDto.class))).thenReturn(ingredientDto);

        mockMvc.perform(put("/api/ingredients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/ingredients/{id} - Supprimer avec succès")
    void testDeleteIngredient_Success() throws Exception {
        doNothing().when(ingredientService).delete(1L);

        mockMvc.perform(delete("/api/ingredients/1"))
                .andExpect(status().isNoContent());
    }

    // ==================== Tests SPECIFIQUES (Alerte, Stock) ====================

    @Test
    @DisplayName("PATCH /api/ingredients/{id}/ajouter - Augmenter le stock")
    void testAjouterQuantite_Success() throws Exception {
        BigDecimal ajout = new BigDecimal("5.0");
        when(ingredientService.ajouterQuantite(eq(1L), any(BigDecimal.class))).thenReturn(ingredientDto);

        mockMvc.perform(patch("/api/ingredients/1/ajouter")
                        .param("quantite", ajout.toString()))
                .andExpect(status().isOk());
    }

//    @Test
//    @DisplayName("PATCH /api/ingredients/{id}/retirer - Quantité insuffisante (400)")
//    void testRetirerQuantite_InsufficientStock() throws Exception {
//        when(ingredientService.retirerQuantite(eq(1L), any(BigDecimal.class)))
//                .thenThrow(new RuntimeException("Quantité insuffisante"));
//
//        mockMvc.perform(patch("/api/ingredients/1/retirer")
//                        .param("quantite", "100.0"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Quantité insuffisante"));
//    }

    @Test
    @DisplayName("GET /api/ingredients/alertes - Récupérer les alertes")
    void testGetIngredientsEnAlerte() throws Exception {
        when(ingredientService.findIngredientsEnAlerte()).thenReturn(Arrays.asList(ingredientDto));

        mockMvc.perform(get("/api/ingredients/alertes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

//    @Test
//    @DisplayName("GET /api/ingredients/statistiques - Vérifier les stats")
//    void testGetStatistiques_Success() throws Exception {
//        when(ingredientService.getAll()).thenReturn(Arrays.asList(ingredientDto));
//        when(ingredientService.findIngredientsEnAlerte()).thenReturn(List.of());
//
//        mockMvc.perform(get("/api/ingredients/statistiques"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.totalIngredients").value(1))
//                .andExpect(jsonPath("$.ingredientsEnAlerte").value(0));
//    }
}