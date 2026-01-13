package com.example.BackendProject.TestController;

import com.example.BackendProject.controllers.AuthController;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.security.JwtUtils;
import com.example.BackendProject.services.implementations.SmsService;
import com.example.BackendProject.services.implementations.UtilisateurDetailService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

        private MockMvc mockMvc;

        @Mock
        private AuthenticationManager authManager;
        @Mock
        private UtilisateurDetailService utilisateurDetailService;
        @Mock
        private JwtUtils jwtUtils;
        @Mock
        private SmsService smsService;
        @Mock
        private UtilisateurRepository utilisateurRepository;

        @InjectMocks
        private AuthController authController;

        private ObjectMapper objectMapper = new ObjectMapper();
        private Utilisateur mockUser;
        private UtilisateurDto loginRequest;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(authController)
                                .setMessageConverters(
                                                new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter())
                                .build();

                // Initialisation d'un utilisateur de test
                mockUser = new Utilisateur();
                mockUser.setId(1L);
                mockUser.setEmail("test@example.com");
                mockUser.setMotDePasse("password123");
                mockUser.setNom("Jean Dupont");
                mockUser.setTelephone("+221770000000");
                mockUser.setRole(RoleType.MANAGER); // Assurez-vous que l'Enum Role.USER existe

                loginRequest = new UtilisateurDto();
                loginRequest.setEmail("test@example.com");
                loginRequest.setMotDePasse("password123");
        }

        @Test
        @DisplayName("Login - Succès (Étape 1 du 2FA)")
        void login_Success() throws Exception {
                // Simuler l'authentification réussie
                when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
                when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

                mockMvc.perform(post("/api/Auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("PENDING_2FA"))
                                .andExpect(jsonPath("$.username").value("Jean Dupont"));

                // Vérifier que le code a été généré et "envoyé" par SMS
                verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
                verify(smsService, times(1)).sendSms(eq(mockUser.getTelephone()), anyString());
        }

        @Test
        @DisplayName("Login - Échec (Mauvais identifiants)")
        void login_BadCredentials() throws Exception {
                when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new BadCredentialsException("Invalid credentials"));

                mockMvc.perform(post("/api/Auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().string("Email ou mot de passe incorrect"));
        }

        @Test
        @DisplayName("Verify 2FA - Succès")
        void verify2fa_Success() throws Exception {
                // 1. Préparer l'utilisateur avec un code valide
                String validCode = "1234";
                mockUser.setVerificationCode(validCode);
                mockUser.setExpiryCode(LocalDateTime.now().plusMinutes(5));

                // On doit simuler le fait que le login a déjà eu lieu (remplir la map interne
                // via réflexion ou en appelant login)
                // Mais comme c'est un test unitaire, on va simuler les appels repository
                when(utilisateurRepository.findByNom("Jean Dupont")).thenReturn(mockUser);

                UserDetails userDetails = new User(mockUser.getEmail(), mockUser.getMotDePasse(),
                                Collections.emptyList());
                when(utilisateurDetailService.loadUserByUsername(mockUser.getEmail())).thenReturn(userDetails);
                when(jwtUtils.generateToken(userDetails)).thenReturn("mock-jwt-token");

                // Simuler que le nom est présent dans pendingVerifications
                // Note: Dans un test standalone, on peut appeler login juste avant ou injecter
                // la map si elle était protected
                // Ici, on va d'abord appeler login pour remplir la map
                when(authManager.authenticate(any())).thenReturn(null);
                when(utilisateurRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
                mockMvc.perform(post("/api/Auth/login").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)));

                // 2. Tester la vérification
                Map<String, String> verifyRequest = Map.of(
                                "username", "Jean Dupont",
                                "code", validCode);

                mockMvc.perform(post("/api/Auth/verify-2fa")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(verifyRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                                .andExpect(jsonPath("$.username").value("Jean Dupont"));
        }

        @Test
        @DisplayName("Verify 2FA - Code Incorrect")
        void verify2fa_WrongCode() throws Exception {
                mockUser.setVerificationCode("1234");
                mockUser.setExpiryCode(LocalDateTime.now().plusMinutes(5));

                when(utilisateurRepository.findByNom("Jean Dupont")).thenReturn(mockUser);

                // On remplit la map via un appel login
                when(authManager.authenticate(any())).thenReturn(null);
                when(utilisateurRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
                mockMvc.perform(post("/api/Auth/login").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)));

                Map<String, String> verifyRequest = Map.of(
                                "username", "Jean Dupont",
                                "code", "9999" // Mauvais code
                );

                mockMvc.perform(post("/api/Auth/verify-2fa")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(verifyRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().string("Code incorrect ou expiré"));
        }

        @Test
        @DisplayName("Logout - Succès")
        void logout_Success() throws Exception {
                Map<String, String> logoutRequest = Map.of("username", "Jean Dupont");

                mockMvc.perform(post("/api/Auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(logoutRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(containsString("Déconnexion réussie")));
        }
}