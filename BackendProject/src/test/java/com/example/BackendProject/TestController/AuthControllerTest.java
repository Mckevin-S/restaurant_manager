package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.AuthController;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.security.JwtUtils;
import com.example.BackendProject.services.implementations.UtilisateurDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du contrôleur d'authentification")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UtilisateurDetailService utilisateurDetailService;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Login réussi - Doit retourner un token JWT")
    void testLogin_Success() throws Exception {
        // Arrange
        UtilisateurDto loginRequest = new UtilisateurDto();
        loginRequest.setNom("testuser");
        loginRequest.setMotDePasse("password123");

        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(new ArrayList<>())
                .build();

        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        // Mock des dépendances
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // L'authentification réussit

        when(utilisateurDetailService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);

        when(jwtUtils.generateToken(userDetails))
                .thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));

        // Verify
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(utilisateurDetailService, times(1)).loadUserByUsername("testuser");
        verify(jwtUtils, times(1)).generateToken(userDetails);
    }

    @Test
    @DisplayName("Login échoué - Identifiants incorrects")
    void testLogin_BadCredentials() throws Exception {
        // Arrange
        UtilisateurDto loginRequest = new UtilisateurDto();
        loginRequest.setNom("testuser");
        loginRequest.setMotDePasse("wrongpassword");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Identifiants incorrects")));

        // Verify
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(utilisateurDetailService, never()).loadUserByUsername(anyString());
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    @DisplayName("Login échoué - Utilisateur inexistant")
    void testLogin_UserNotFound() throws Exception {
        // Arrange
        UtilisateurDto loginRequest = new UtilisateurDto();
        loginRequest.setNom("nonexistent");
        loginRequest.setMotDePasse("password123");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        // Verify
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    @DisplayName("Login échoué - Erreur serveur")
    void testLogin_InternalServerError() throws Exception {
        // Arrange
        UtilisateurDto loginRequest = new UtilisateurDto();
        loginRequest.setNom("testuser");
        loginRequest.setMotDePasse("password123");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Erreur technique")));

        // Verify
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    @DisplayName("Login avec nom vide")
    void testLogin_EmptyUsername() throws Exception {
        // Arrange
        UtilisateurDto loginRequest = new UtilisateurDto();
        loginRequest.setNom("");
        loginRequest.setMotDePasse("password123");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Empty username"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login avec mot de passe vide")
    void testLogin_EmptyPassword() throws Exception {
        // Arrange
        UtilisateurDto loginRequest = new UtilisateurDto();
        loginRequest.setNom("testuser");
        loginRequest.setMotDePasse("");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Empty password"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login avec corps de requête invalide")
    void testLogin_InvalidRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Vérifier que le token généré n'est pas null")
    void testLogin_TokenNotNull() throws Exception {
        // Arrange
        UtilisateurDto loginRequest = new UtilisateurDto();
        loginRequest.setNom("testuser");
        loginRequest.setMotDePasse("password123");

        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(new ArrayList<>())
                .build();

        String expectedToken = "validToken123";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(utilisateurDetailService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails))
                .thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
