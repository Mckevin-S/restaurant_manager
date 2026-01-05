package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.PlatController;
import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.services.implementations.PlatServiceImplementation;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - PlatController")
class PlatControllerTest {

    @Mock
    private PlatServiceImplementation platService;

    @InjectMocks
    private PlatController platController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PlatDto platDto;

    /**
     * Gestionnaire d'exceptions local pour simuler le comportement du contrôleur
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
        mockMvc = MockMvcBuilders.standaloneSetup(platController)
                .setControllerAdvice(new TestGlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        platDto = new PlatDto();
        platDto.setId(1L);
        platDto.setNom("Burger Maison");
        platDto.setPrix(new BigDecimal("15.00"));
        platDto.setDisponibilite(true);
        platDto.setCategory(2L);
    }

    // ==================== Tests CRUD de base ====================

    @Test
    @DisplayName("POST /api/plats - Créer un plat avec succès")
    void testCreatePlat_Success() throws Exception {
        when(platService.save(any(PlatDto.class))).thenReturn(platDto);

        mockMvc.perform(post("/api/plats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(platDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Burger Maison"));
    }

    @Test
    @DisplayName("GET /api/plats/{id} - Plat non trouvé (404)")
    void testGetPlatById_NotFound() throws Exception {
        when(platService.getById(99L)).thenThrow(new RuntimeException("Plat non trouvé avec l'ID : 99"));

        mockMvc.perform(get("/api/plats/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/plats/{id} - Suppression réussie")
    void testDeletePlat_Success() throws Exception {
        doNothing().when(platService).delete(1L);

        mockMvc.perform(delete("/api/plats/1"))
                .andExpect(status().isNoContent());
    }

    // ==================== Tests Spécifiques (Menu & Stats) ====================

    @Test
    @DisplayName("GET /api/plats/disponibles - Récupérer la carte")
    void testGetMenu_Success() throws Exception {
        when(platService.getMenuActif()).thenReturn(Arrays.asList(platDto));

        mockMvc.perform(get("/api/plats/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].disponibilite").value(true));
    }

    @Test
    @DisplayName("GET /api/plats/plus-vendus - Statistiques de ventes")
    void testGetTopPlats_Success() throws Exception {
        Map<String, Object> stat = new HashMap<>();
        stat.put("nom", "Burger Maison");
        stat.put("ventes", 50);

        when(platService.getStatistiquesPlatsVendus()).thenReturn(Arrays.asList(stat));

        mockMvc.perform(get("/api/plats/plus-vendus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Burger Maison"))
                .andExpect(jsonPath("$[0].ventes").value(50));
    }

    // ==================== Tests Patch & Upload ====================

    @Test
    @DisplayName("PATCH /api/plats/{id}/statut-disponibilite - Changer disponibilité")
    void testUpdateStatus_Success() throws Exception {
        platDto.setDisponibilite(false);
        when(platService.modifierDisponibilite(eq(1L), eq(false))).thenReturn(platDto);

        mockMvc.perform(patch("/api/plats/1/statut-disponibilite")
                        .param("disponible", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponibilite").value(false));
    }

    @Test
    @DisplayName("POST /api/plats/{id}/upload-image - Upload d'image")
    void testUploadImage_Success() throws Exception {
        // Simulation d'un fichier multipart
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "burger.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenu image".getBytes()
        );

        when(platService.uploadPlatImage(eq(1L), any())).thenReturn(platDto);

        mockMvc.perform(multipart("/api/plats/1/upload-image")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
