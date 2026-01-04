package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.LoginResponse;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.security.JwtUtils;
import com.example.BackendProject.services.implementations.UtilisateurDetailService;
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
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authManager,
                          UtilisateurDetailService utilisateurDetailService,
                          JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.utilisateurDetailService = utilisateurDetailService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UtilisateurDto request) {
        try {
            // 1. Authentification
            var authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getNom(),
                            request.getMotDePasse()
                    )
            );

            // 2. Récupération des détails et du rôle
            var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            // On récupère le rôle (ex: "ROLE_MANAGER")
            String role = userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse("ROLE_USER");

            // 3. Génération du token
            String token = jwtUtils.generateToken(userDetails);

            // 4. Retourne la réponse complète pour le Frontend
            return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername(), role));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nom d'utilisateur ou mot de passe incorrect.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la connexion.");
        }
    }
}
