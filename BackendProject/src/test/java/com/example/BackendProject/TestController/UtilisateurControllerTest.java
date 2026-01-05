package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.UtilisateurController;
import com.example.BackendProject.dto.ChangePasswordRequest;
import com.example.BackendProject.dto.ResetPasswordRequest;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.services.implementations.UtilisateurServiceImplementation;
import com.example.BackendProject.utils.RoleType;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - UtilisateurController")
class UtilisateurControllerTest {

    @Mock
    private UtilisateurServiceImplementation utilisateurService;

    @InjectMocks
    private UtilisateurController utilisateurController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UtilisateurDto userDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();
        objectMapper = new ObjectMapper();

        userDto = new UtilisateurDto();
        userDto.setId(1L);
        userDto.setNom("Dupont");
        userDto.setPrenom("Jean");
        userDto.setEmail("jean.dupont@example.com");
        userDto.setRole(RoleType.SERVEUR);
    }

    // ==================== CRUD DE BASE ====================

    @Test
    @DisplayName("POST /api/users - Création utilisateur")
    void testCreate_Success() throws Exception {
        when(utilisateurService.save(any(UtilisateurDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.role").value("SERVEUR"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Utilisateur trouvé")
    void testGetUserById_Success() throws Exception {
        when(utilisateurService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Suppression")
    void testDeleteUser_Success() throws Exception {
        doNothing().when(utilisateurService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    // ==================== RECHERCHE & FILTRES ====================

    @Test
    @DisplayName("GET /api/users/role/{role} - Filtrer par rôle")
    void testGetUsersByRole_Success() throws Exception {
        when(utilisateurService.findByRoleType(RoleType.SERVEUR)).thenReturn(Arrays.asList(userDto));

        mockMvc.perform(get("/api/users/role/SERVEUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].role").value("SERVEUR"));
    }

    @Test
    @DisplayName("GET /api/users/search - Recherche par mot-clé")
    void testSearchUsers_Success() throws Exception {
        when(utilisateurService.search("Jean")).thenReturn(Arrays.asList(userDto));

        mockMvc.perform(get("/api/users/search").param("keyword", "Jean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prenom").value("Jean"));
    }

    // ==================== MOTS DE PASSE ====================

    @Test
    @DisplayName("PUT /api/users/{id}/change-password - Succès")
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("Ancien123");
        request.setNewPassword("Nouveau123");

        doNothing().when(utilisateurService).changePassword(eq(1L), anyString(), anyString());

        mockMvc.perform(put("/api/users/1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mot de passe modifié avec succès"));
    }

    @Test
    @DisplayName("PUT /api/users/{id}/change-password - Échec (Mauvais MDP)")
    void testChangePassword_WrongPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("FauxMDP");
        request.setNewPassword("Nouveau123");

        // On simule une exception lancée par le service
        doThrow(new RuntimeException("L'ancien mot de passe est incorrect"))
                .when(utilisateurService).changePassword(eq(1L), eq("FauxMDP"), anyString());

        mockMvc.perform(put("/api/users/1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'ancien mot de passe est incorrect"));
    }

    @Test
    @DisplayName("PUT /api/users/{id}/reset-password - Succès")
    void testResetPassword_Success() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword("AdminReset123");

        doNothing().when(utilisateurService).resetPassword(eq(1L), anyString());

        mockMvc.perform(put("/api/users/1/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mot de passe réinitialisé avec succès"));
    }
}
