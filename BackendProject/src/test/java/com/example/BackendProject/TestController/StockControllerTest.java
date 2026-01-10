package com.example.BackendProject.TestController;
import com.example.BackendProject.controllers.StockMovementController;
import com.example.BackendProject.dto.StockMovementDto;
import com.example.BackendProject.entities.Ingredient;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - StockMovementController")
public class StockControllerTest {
    @Mock
    private StockMovementServiceInterface stockMovementService;

    @InjectMocks
    private StockMovementController stockMovementController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private StockMovementDto stockMovementDto;
    private List<StockMovementDto> stockMovementList;
   private Ingredient ingredient;

    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
            return new ResponseEntity<>("Erreur de validation: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("non trouvé")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            return new ResponseEntity<>("Erreur interne: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stockMovementController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // Création d'un ingrédient pour les tests
        ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setNom("Farine");


        // Initialisation des données de test
        stockMovementDto = new StockMovementDto(
                1L,
                ingredient.getId(),
                TypeMouvement.ENTREE,
                new BigDecimal("100.00"),
                new Timestamp(System.currentTimeMillis()),
                "Approvisionnement initial"
        );



       // stockMovementList = Arrays.asList(stockMovementDto, stockMovementDto2);
    }



    @Test
    @DisplayName("POST - Créer un mouvement avec succès")
    void createStockMovement_Success() throws Exception {
        when(stockMovementService.createStockMovement(any(StockMovementDto.class)))
                .thenReturn(stockMovementDto);

        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockMovementDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ingredient.id").value(ingredient.getId()))
                .andExpect(jsonPath("$.typeMouvement").value("ENTREE"))
                .andExpect(jsonPath("$.quantite").value(100.00))
                .andExpect(jsonPath("$.raison").value("Approvisionnement initial"));

        verify(stockMovementService, times(1)).createStockMovement(any(StockMovementDto.class));
    }

    @Test
    @DisplayName("POST - Erreur de validation")
    void createStockMovement_ValidationError() throws Exception {
        when(stockMovementService.createStockMovement(any(StockMovementDto.class)))
                .thenThrow(new IllegalArgumentException("La quantité doit être positive"));

        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockMovementDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Erreur de validation")));

        verify(stockMovementService, times(1)).createStockMovement(any(StockMovementDto.class));
    }

    @Test
    @DisplayName("POST - Erreur serveur interne")
    void createStockMovement_InternalServerError() throws Exception {
        when(stockMovementService.createStockMovement(any(StockMovementDto.class)))
                .thenThrow(new RuntimeException("Erreur base de données"));

        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockMovementDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }



    @Test
    @DisplayName("PUT - Mettre à jour avec succès")
    void updateStockMovement_Success() throws Exception {
        Long id = 1L;
        stockMovementDto.setRaison("Raison mise à jour");
        when(stockMovementService.updateStockMovement(eq(id), any(StockMovementDto.class)))
                .thenReturn(stockMovementDto);

        mockMvc.perform(put("/api/stock-movements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockMovementDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.raison").value("Raison mise à jour"));

        verify(stockMovementService, times(1)).updateStockMovement(eq(id), any(StockMovementDto.class));
    }

    @Test
    @DisplayName("PUT - Mouvement non trouvé")
    void updateStockMovement_NotFound() throws Exception {
        Long id = 999L;
        when(stockMovementService.updateStockMovement(eq(id), any(StockMovementDto.class)))
                .thenThrow(new RuntimeException("Mouvement non trouvé"));

        mockMvc.perform(put("/api/stock-movements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockMovementDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT - Erreur de validation")
    void updateStockMovement_ValidationError() throws Exception {
        Long id = 1L;
        when(stockMovementService.updateStockMovement(eq(id), any(StockMovementDto.class)))
                .thenThrow(new IllegalArgumentException("Quantité invalide"));

        mockMvc.perform(put("/api/stock-movements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockMovementDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Erreur de validation")));
    }



//    @Test
//    @DisplayName("GET - Récupérer tous les mouvements")
//    void getAllStockMovements_Success() throws Exception {
//        when(stockMovementService.getAllStockMovements()).thenReturn(stockMovementList);
//
//        mockMvc.perform(get("/api/stock-movements")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[0].typeMouvement").value("ENTREE"))
//                .andExpect(jsonPath("$[1].typeMouvement").value("SORTIE"));
//
//        verify(stockMovementService, times(1)).getAllStockMovements();
//    }

    @Test
    @DisplayName("GET - Liste vide")
    void getAllStockMovements_EmptyList() throws Exception {
        when(stockMovementService.getAllStockMovements()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(stockMovementService, times(1)).getAllStockMovements();
    }

    @Test
    @DisplayName("GET - Erreur serveur")
    void getAllStockMovements_InternalServerError() throws Exception {
        when(stockMovementService.getAllStockMovements())
                .thenThrow(new RuntimeException("Erreur base de données"));

        mockMvc.perform(get("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    // ==================== Tests GET /api/stock-movements/{id} ====================

    @Test
    @DisplayName("GET by ID - Récupérer un mouvement par ID")
    void getStockMovementById_Success() throws Exception {
        Long id = 1L;
        when(stockMovementService.getStockMovementById(id)).thenReturn(stockMovementDto);

        mockMvc.perform(get("/api/stock-movements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ingredient.id").value(1))
                .andExpect(jsonPath("$.typeMouvement").value("ENTREE"));

        verify(stockMovementService, times(1)).getStockMovementById(id);
    }

    @Test
    @DisplayName("GET by ID - Mouvement non trouvé")
    void getStockMovementById_NotFound() throws Exception {
        Long id = 999L;
        when(stockMovementService.getStockMovementById(id))
                .thenThrow(new RuntimeException("Mouvement non trouvé"));

        mockMvc.perform(get("/api/stock-movements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    @Test
    @DisplayName("DELETE - Supprimer avec succès")
    void deleteStockMovement_Success() throws Exception {
        Long id = 1L;
        doNothing().when(stockMovementService).deleteStockMovement(id);

        mockMvc.perform(delete("/api/stock-movements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(stockMovementService, times(1)).deleteStockMovement(id);
    }

    @Test
    @DisplayName("DELETE - Mouvement non trouvé")
    void deleteStockMovement_NotFound() throws Exception {
        Long id = 999L;
        doThrow(new RuntimeException("Mouvement non trouvé"))
                .when(stockMovementService).deleteStockMovement(id);

        mockMvc.perform(delete("/api/stock-movements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

//    @Test
//    @DisplayName("DELETE - Erreur serveur")
//    void deleteStockMovement_InternalServerError() throws Exception {
//        Long id = 1L;
//        doThrow(new Exception("Erreur base de données"))
//                .when(stockMovementService).deleteStockMovement(id);
//
//        mockMvc.perform(delete("/api/stock-movements/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isInternalServerError());
//    }

    // ==================== Tests GET /api/stock-movements/ingredient/{ingredientId} ====================

//    @Test
//    @DisplayName("GET by Ingredient - Récupérer mouvements par ingrédient")
//    void getStockMovementsByIngredient_Success() throws Exception {
//        Long ingredientId = 1L;
//        when(stockMovementService.getStockMovementsByIngredientId(ingredientId))
//                .thenReturn(stockMovementList);
//
//        mockMvc.perform(get("/api/stock-movements/ingredient/{ingredientId}", ingredientId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].ingredient.id").value(1))
//                .andExpect(jsonPath("$[1].ingredient.id").value(1));
//
//        verify(stockMovementService, times(1)).getStockMovementsByIngredientId(ingredientId);
//    }

    @Test
    @DisplayName("GET by Ingredient - Ingrédient non trouvé")
    void getStockMovementsByIngredient_NotFound() throws Exception {
        Long ingredientId = 999L;
        when(stockMovementService.getStockMovementsByIngredientId(ingredientId))
                .thenThrow(new RuntimeException("Ingrédient non trouvé"));

        mockMvc.perform(get("/api/stock-movements/ingredient/{ingredientId}", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET by Ingredient - Liste vide")
    void getStockMovementsByIngredient_EmptyList() throws Exception {
        Long ingredientId = 1L;
        when(stockMovementService.getStockMovementsByIngredientId(ingredientId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/stock-movements/ingredient/{ingredientId}", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== Tests GET /api/stock-movements/type/{typeMouvement} ====================

    @Test
    @DisplayName("GET by Type - Récupérer mouvements par type ENTREE")
    void getStockMovementsByType_Entree() throws Exception {
        List<StockMovementDto> entreeList = Collections.singletonList(stockMovementDto);
        when(stockMovementService.getStockMovementsByType(TypeMouvement.ENTREE))
                .thenReturn(entreeList);

        mockMvc.perform(get("/api/stock-movements/type/{typeMouvement}", "ENTREE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].typeMouvement").value("ENTREE"));

        verify(stockMovementService, times(1)).getStockMovementsByType(TypeMouvement.ENTREE);
    }

//    @Test
//    @DisplayName("GET by Type - Récupérer mouvements par type SORTIE")
//    void getStockMovementsByType_Sortie() throws Exception {
//        StockMovementDto sortieDto = stockMovementList.get(1);
//        List<StockMovementDto> sortieList = Collections.singletonList(sortieDto);
//        when(stockMovementService.getStockMovementsByType(TypeMouvement.SORTIE))
//                .thenReturn(sortieList);
//
//        mockMvc.perform(get("/api/stock-movements/type/{typeMouvement}", "SORTIE")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].typeMouvement").value("SORTIE"));
//
//        verify(stockMovementService, times(1)).getStockMovementsByType(TypeMouvement.SORTIE);
//    }

    // ==================== Tests GET /api/stock-movements/between ====================

//    @Test
//    @DisplayName("GET between dates - Récupérer mouvements entre deux dates")
//    void getStockMovementsBetweenDates_Success() throws Exception {
//        String startDate = "2024-01-01 00:00:00";
//        String endDate = "2024-12-31 23:59:59";
//
//        when(stockMovementService.getStockMovementsBetweenDates(
//                any(Timestamp.class), any(Timestamp.class)))
//                .thenReturn(stockMovementList);
//
//        mockMvc.perform(get("/api/stock-movements/between")
//                        .param("startDate", startDate)
//                        .param("endDate", endDate)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[1].id").value(2));
//
//        verify(stockMovementService, times(1))
//                .getStockMovementsBetweenDates(any(Timestamp.class), any(Timestamp.class));
//    }

    @Test
    @DisplayName("GET between dates - Format de date invalide")
    void getStockMovementsBetweenDates_InvalidDateFormat() throws Exception {
        String startDate = "2024/01/01";
        String endDate = "2024/12/31";

        mockMvc.perform(get("/api/stock-movements/between")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

//    @Test
//    @DisplayName("GET between dates - Dates manquantes")
//    void getStockMovementsBetweenDates_MissingParameters() throws Exception {
//        mockMvc.perform(get("/api/stock-movements/between")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }

    @Test
    @DisplayName("GET between dates - Liste vide")
    void getStockMovementsBetweenDates_EmptyList() throws Exception {
        String startDate = "2024-01-01 00:00:00";
        String endDate = "2024-12-31 23:59:59";

        when(stockMovementService.getStockMovementsBetweenDates(
                any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/stock-movements/between")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== Tests GET /api/stock-movements/ingredient/{ingredientId}/total ====================

    @Test
    @DisplayName("GET Total - Calculer quantité totale")
    void getTotalQuantityByIngredient_Success() throws Exception {
        Long ingredientId = 1L;
        BigDecimal totalQuantity = new BigDecimal("150.00");
        when(stockMovementService.getTotalQuantityByIngredient(ingredientId))
                .thenReturn(totalQuantity);

        mockMvc.perform(get("/api/stock-movements/ingredient/{ingredientId}/total", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("150.00"));

        verify(stockMovementService, times(1)).getTotalQuantityByIngredient(ingredientId);
    }

    @Test
    @DisplayName("GET Total - Quantité zéro")
    void getTotalQuantityByIngredient_ZeroQuantity() throws Exception {
        Long ingredientId = 1L;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        when(stockMovementService.getTotalQuantityByIngredient(ingredientId))
                .thenReturn(totalQuantity);

        mockMvc.perform(get("/api/stock-movements/ingredient/{ingredientId}/total", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("GET Total - Ingrédient non trouvé")
    void getTotalQuantityByIngredient_NotFound() throws Exception {
        Long ingredientId = 999L;
        when(stockMovementService.getTotalQuantityByIngredient(ingredientId))
                .thenThrow(new RuntimeException("Ingrédient non trouvé"));

        mockMvc.perform(get("/api/stock-movements/ingredient/{ingredientId}/total", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

//    @Test
//    @DisplayName("GET Total - Erreur de calcul")
//    void getTotalQuantityByIngredient_CalculationError() throws Exception {
//        Long ingredientId = 1L;
//        when(stockMovementService.getTotalQuantityByIngredient(ingredientId))
//                .thenThrow(new Exception("Erreur de calcul"));
//
//        mockMvc.perform(get("/api/stock-movements/ingredient/{ingredientId}/total", ingredientId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isInternalServerError());
//    }
}