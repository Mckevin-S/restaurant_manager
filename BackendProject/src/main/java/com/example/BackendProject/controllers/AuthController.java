package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.LoginResponse;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.security.JwtUtils;
import com.example.BackendProject.services.implementations.UtilisateurDetailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UtilisateurDetailService utilisateurDetailService;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authManager, UtilisateurDetailService utilisateurDetailService, UtilisateurRepository utilisateurRepository, JwtUtils jwtUtils, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.utilisateurDetailService = utilisateurDetailService;
        this.utilisateurRepository = utilisateurRepository;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
    }

    /**
     * Endpoint de connexion.
     * @return JWT token si authentification réussie
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UtilisateurDto request) {
        try {
            // Authentification via Spring Security
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getNom(),
                            request.getMotDePasse()
                    )
            );

            // Génération du token JWT
            var userDetails = utilisateurDetailService.loadUserByUsername(request.getEmail());
            String token = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            // ✅ CORRIGÉ : Concaténation correcte pour le message d'erreur
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants incorrects pour l'utilisateur : " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'authentification");
        }
    }



}
