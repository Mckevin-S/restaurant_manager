package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.OrderItemOptionController;
import com.example.BackendProject.dto.OrderItemOptionDto;
import com.example.BackendProject.services.interfaces.OrderItemOptionServiceInterface;
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
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - OrderItemOptionController")
class OrderItemOptionControllerTest {

    @Mock
    private OrderItemOptionServiceInterface orderItemOptionService;

    @InjectMocks
    private OrderItemOptionController orderItemOptionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OrderItemOptionDto orderOptionDto;

    /**
     * Gestionnaire d'exceptions local pour simuler le comportement de l'API
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
        mockMvc = MockMvcBuilders.standaloneSetup(orderItemOptionController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        orderOptionDto = new OrderItemOptionDto();
        orderOptionDto.setId(1L);
        orderOptionDto.setLigneCommande(10L);
        orderOptionDto.setOption(5L);
        orderOptionDto.setNomOption("Supplément Sauce");
    }

    // ==================== Tests CREATE ====================

    @Test
    @DisplayName("POST /api/order-item-options - Ajouter une option avec succès")
    void testCreate_Success() throws Exception {
        when(orderItemOptionService.save(any(OrderItemOptionDto.class))).thenReturn(orderOptionDto);

        mockMvc.perform(post("/api/order-item-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderOptionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomOption").value("Supplément Sauce"));
    }

    // ==================== Tests READ ====================

    @Test
    @DisplayName("GET /api/order-item-options/ligne-commande/{id} - Lister les options")
    void testGetByLigne_Success() throws Exception {
        when(orderItemOptionService.getByLigneCommande(10L)).thenReturn(Arrays.asList(orderOptionDto));

        mockMvc.perform(get("/api/order-item-options/ligne-commande/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ligneCommande").value(10));
    }

    @Test
    @DisplayName("GET /api/order-item-options/ligne-commande/{id} - Ligne non trouvée (404)")
    void testGetByLigne_NotFound() throws Exception {
        when(orderItemOptionService.getByLigneCommande(99L))
                .thenThrow(new RuntimeException("Ligne de commande non trouvée"));

        mockMvc.perform(get("/api/order-item-options/ligne-commande/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== Tests DELETE ====================

    @Test
    @DisplayName("DELETE /api/order-item-options/{id} - Suppression réussie")
    void testDelete_Success() throws Exception {
        doNothing().when(orderItemOptionService).delete(1L);

        mockMvc.perform(delete("/api/order-item-options/1"))
                .andExpect(status().isNoContent());
    }
}
