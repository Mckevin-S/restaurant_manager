package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.services.implementations.UtilisateurServiceImplementation;
import com.example.BackendProject.utils.RoleType;
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

import java.time.LocalDate;
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
@DisplayName("Tests d'Intégration du Controller Utilisateur")
class UtilisateurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UtilisateurServiceImplementation utilisateurService;

    private UtilisateurDto utilisateurDto;

    @BeforeEach
    void setUp() {
        utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(1L);
        utilisateurDto.setNom("Dupont");
        utilisateurDto.setPrenom("Jean");
        utilisateurDto.setEmail("jean.dupont@restaurant.com");
        utilisateurDto.setMotDePasse("Password123!");
        utilisateurDto.setTelephone("+237690000000");
        utilisateurDto.setRole(RoleType.SERVEUR);
        utilisateurDto.setDateEmbauche(LocalDate.now());
    }

    @Test
    @DisplayName("GET /api/users - Devrait retourner tous les utilisateurs")
    @WithMockUser(roles = "MANAGER")
    void devraitRetournerTousLesUtilisateurs() throws Exception {
        List<UtilisateurDto> utilisateurs = Arrays.asList(utilisateurDto);
        when(utilisateurService.getAll()).thenReturn(utilisateurs);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nom").value("Dupont"))
                .andExpect(jsonPath("$[0].email").value("jean.dupont@restaurant.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Devrait retourner un utilisateur par ID")
    @WithMockUser(roles = "MANAGER")
    void devraitRetournerUtilisateurParId() throws Exception {
        when(utilisateurService.getById(1L)).thenReturn(utilisateurDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Dupont"));
    }

    @Test
    @DisplayName("POST /api/users - Devrait créer un nouvel utilisateur")
    @WithMockUser(roles = "MANAGER")
    void devraitCreerNouvelUtilisateur() throws Exception {
        when(utilisateurService.save(any(UtilisateurDto.class))).thenReturn(utilisateurDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateurDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@restaurant.com"));
    }

    @Test
    @DisplayName("POST /api/users - Devrait rejeter un utilisateur avec email invalide")
    @WithMockUser(roles = "MANAGER")
    void devraitRejeterUtilisateurEmailInvalide() throws Exception {
        UtilisateurDto utilisateurInvalide = new UtilisateurDto();
        utilisateurInvalide.setNom("Test");
        utilisateurInvalide.setPrenom("User");
        utilisateurInvalide.setEmail("email-invalide"); // Email invalide
        utilisateurInvalide.setMotDePasse("Pass123!");
        utilisateurInvalide.setTelephone("+237690000000");
        utilisateurInvalide.setRole(RoleType.SERVEUR);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateurInvalide)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Devrait rejeter un utilisateur avec mot de passe court")
    @WithMockUser(roles = "MANAGER")
    void devraitRejeterUtilisateurMotDePasseCourt() throws Exception {
        UtilisateurDto utilisateurInvalide = new UtilisateurDto();
        utilisateurInvalide.setNom("Test");
        utilisateurInvalide.setPrenom("User");
        utilisateurInvalide.setEmail("test@restaurant.com");
        utilisateurInvalide.setMotDePasse("123"); // Trop court
        utilisateurInvalide.setTelephone("+237690000000");
        utilisateurInvalide.setRole(RoleType.SERVEUR);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateurInvalide)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Devrait mettre à jour un utilisateur")
    @WithMockUser(roles = "MANAGER")
    void devraitMettreAJourUtilisateur() throws Exception {
        UtilisateurDto utilisateurMisAJour = new UtilisateurDto();
        utilisateurMisAJour.setId(1L);
        utilisateurMisAJour.setNom("Martin");
        utilisateurMisAJour.setPrenom("Pierre");
        utilisateurMisAJour.setEmail("pierre.martin@restaurant.com");
        utilisateurMisAJour.setTelephone("+237690000001");
        utilisateurMisAJour.setRole(RoleType.MANAGER);

        when(utilisateurService.update(anyLong(), any(UtilisateurDto.class)))
                .thenReturn(utilisateurMisAJour);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateurMisAJour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Martin"))
                .andExpect(jsonPath("$.email").value("pierre.martin@restaurant.com"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Devrait supprimer un utilisateur")
    @WithMockUser(roles = "MANAGER")
    void devraitSupprimerUtilisateur() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/users/role/{roleType} - Devrait retourner les utilisateurs par rôle")
    @WithMockUser(roles = "MANAGER")
    void devraitRetournerUtilisateursParRole() throws Exception {
        when(utilisateurService.findByRoleType(RoleType.SERVEUR))
                .thenReturn(Arrays.asList(utilisateurDto));

        mockMvc.perform(get("/api/users/role/SERVEUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("SERVEUR"));
    }

    @Test
    @DisplayName("GET /api/users/search - Devrait rechercher des utilisateurs")
    @WithMockUser(roles = "MANAGER")
    void devraitRechercherUtilisateurs() throws Exception {
        when(utilisateurService.search("Dupont")).thenReturn(Arrays.asList(utilisateurDto));

        mockMvc.perform(get("/api/users/search")
                        .param("keyword", "Dupont"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Dupont"));
    }

    @Test
    @DisplayName("GET /api/users - Sans authentification devrait retourner 401")
    void devraitRetourner401SansAuthentification() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}
