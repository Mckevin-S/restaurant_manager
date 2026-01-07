package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.RestaurantController;
import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.services.implementations.RestaurantServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - RestaurantController")
public class RestaurantControllerTest {

    @Mock
    private RestaurantServiceImplementation restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RestaurantDto restaurantDto;

    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handle(RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("non trouvé")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // Création du RestaurantDto avec le constructeur complet
        restaurantDto = new RestaurantDto(
                1L,
                "Restaurant Test",
                "Abidjan",
                "01020304",
                new BigDecimal("18.00"),
                "XOF",
                new Timestamp(System.currentTimeMillis())
        );
    }

    @Test
    @DisplayName("Créer un restaurant - Succès")
    void createRestaurant_OK() throws Exception {
        when(restaurantService.createRestaurant(any())).thenReturn(restaurantDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Restaurant Test"));
    }

    @Test
    @DisplayName("Récupérer tous les restaurants - Succès")
    void getAllRestaurants_OK() throws Exception {
        when(restaurantService.getAllRestaurants()).thenReturn(List.of(restaurantDto));

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Récupérer un restaurant par ID - Succès")
    void getRestaurantById_OK() throws Exception {
        when(restaurantService.getRestaurantById(1L)).thenReturn(restaurantDto);

        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Récupérer un restaurant par ID - Non trouvé")
    void getRestaurantById_NotFound() throws Exception {
        when(restaurantService.getRestaurantById(99L))
                .thenThrow(new RuntimeException("Restaurant non trouvé"));

        mockMvc.perform(get("/api/restaurants/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Mettre à jour un restaurant - Succès")
    void updateRestaurant_OK() throws Exception {
        when(restaurantService.updateRestaurant(eq(1L), any()))
                .thenReturn(restaurantDto);

        mockMvc.perform(put("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Supprimer un restaurant - Succès")
    void deleteRestaurant_OK() throws Exception {
        doNothing().when(restaurantService).deleteRestaurant(1L);

        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());
    }
}