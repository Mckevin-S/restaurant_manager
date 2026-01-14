package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.StockMovementController;
import com.example.BackendProject.dto.StockMovementDto;
import com.example.BackendProject.services.interfaces.StockMovementServiceInterface;
import com.example.BackendProject.utils.TypeMouvement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - StockMovementController")
class StockControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockMovementServiceInterface stockMovementService;

    @InjectMocks
    private StockMovementController stockMovementController;

    private ObjectMapper objectMapper;
    private StockMovementDto sampleDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stockMovementController).build();
        objectMapper = new ObjectMapper();

        // Initialisation d'un DTO de test
        sampleDto = new StockMovementDto();
        sampleDto.setId(1L);
        sampleDto.setIngredientId(10L);
        sampleDto.setTypeMouvement(TypeMouvement.ENTREE);
        sampleDto.setQuantite(new BigDecimal("50.0"));
        sampleDto.setRaison("Livraison fournisseur");
    }

    // ==================== TEST POST (CREATE) ====================

    @Test
    @DisplayName("POST - Créer un mouvement avec succès")
    void createStockMovement_Success() throws Exception {
        when(stockMovementService.createStockMovement(any(StockMovementDto.class))).thenReturn(sampleDto);

        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.typeMouvement").value("ENTREE"))
                .andExpect(jsonPath("$.quantite").value(50.0));
    }

    @Test
    @DisplayName("POST - Erreur 400 sur validation")
    void createStockMovement_BadRequest() throws Exception {
        when(stockMovementService.createStockMovement(any()))
                .thenThrow(new IllegalArgumentException("Quantité négative interdite"));

        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Erreur de validation")));
    }

    // ==================== TEST GET (ALL) ====================

    @Test
    @DisplayName("GET - Récupérer tous les mouvements")
    void getAllStockMovements_Success() throws Exception {
        List<StockMovementDto> list = Arrays.asList(sampleDto, new StockMovementDto());
        when(stockMovementService.getAllStockMovements()).thenReturn(list);

        mockMvc.perform(get("/api/stock-movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ==================== TEST GET (BY ID) ====================

    @Test
    @DisplayName("GET /{id} - Succès")
    void getStockMovementById_Success() throws Exception {
        when(stockMovementService.getStockMovementById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/stock-movements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /{id} - Non trouvé (404)")
    void getStockMovementById_NotFound() throws Exception {
        when(stockMovementService.getStockMovementById(99L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/stock-movements/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== TEST DELETE ====================

    @Test
    @DisplayName("DELETE - Suppression réussie (204)")
    void deleteStockMovement_Success() throws Exception {
        doNothing().when(stockMovementService).deleteStockMovement(1L);

        mockMvc.perform(delete("/api/stock-movements/1"))
                .andExpect(status().isNoContent());

        verify(stockMovementService, times(1)).deleteStockMovement(1L);
    }

    // ==================== TEST FILTRES (BY INGREDIENT / TYPE) ====================

    @Test
    @DisplayName("GET /ingredient/{id} - Succès")
    void getStockMovementsByIngredient_Success() throws Exception {
        when(stockMovementService.getStockMovementsByIngredientId(10L)).thenReturn(Arrays.asList(sampleDto));

        mockMvc.perform(get("/api/stock-movements/ingredient/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ingredientId").value(10));
    }

    @Test
    @DisplayName("GET /type/{type} - Succès")
    void getStockMovementsByType_Success() throws Exception {
        when(stockMovementService.getStockMovementsByType(TypeMouvement.ENTREE)).thenReturn(Arrays.asList(sampleDto));

        mockMvc.perform(get("/api/stock-movements/type/ENTREE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeMouvement").value("ENTREE"));
    }

    // ==================== TEST DATES (BETWEEN) ====================

    @Test
    @DisplayName("GET /between - Succès avec paramètres de date")
    void getStockMovementsBetweenDates_Success() throws Exception {
        String start = "2024-01-01 00:00:00";
        String end = "2024-12-31 23:59:59";

        when(stockMovementService.getStockMovementsBetweenDates(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(Arrays.asList(sampleDto));

        mockMvc.perform(get("/api/stock-movements/between")
                        .param("startDate", start)
                        .param("endDate", end))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /between - Erreur format de date (400)")
    void getStockMovementsBetweenDates_InvalidFormat() throws Exception {
        mockMvc.perform(get("/api/stock-movements/between")
                        .param("startDate", "invalid-date")
                        .param("endDate", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    // ==================== TEST TOTAL (CALCULATION) ====================

    @Test
    @DisplayName("GET /ingredient/{id}/total - Succès")
    void getTotalQuantityByIngredient_Success() throws Exception {
        when(stockMovementService.getTotalQuantityByIngredient(10L)).thenReturn(new BigDecimal("150.75"));

        mockMvc.perform(get("/api/stock-movements/ingredient/10/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.75"));
    }
}