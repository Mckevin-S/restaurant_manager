package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.LoginResponse;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.security.JwtUtils;
import com.example.BackendProject.services.implementations.SmsService;
import com.example.BackendProject.services.implementations.UtilisateurDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UtilisateurDetailService utilisateurDetailService;
    private final JwtUtils jwtUtils;
    private final SmsService smsService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(AuthenticationManager authManager, UtilisateurDetailService utilisateurDetailService, JwtUtils jwtUtils, SmsService smsService, UtilisateurRepository utilisateurRepository) {
        this.authManager = authManager;
        this.utilisateurDetailService = utilisateurDetailService;
        this.jwtUtils = jwtUtils;
        this.smsService = smsService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UtilisateurDto request) {
        try {
            // 1. Vérification classique
            authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getNom(), request.getMotDePasse()));

            Utilisateur user = utilisateurRepository.findByNom(request.getNom());

            // 2. Générer code à 4 chiffres
            String code = String.valueOf((int)(Math.random() * 9000) + 1000);
            user.setVerificationCode(code);
            user.setExpiryCode(LocalDateTime.now().plusMinutes(5));
            utilisateurRepository.save(user);

            // 3. Envoyer SMS
            smsService.sendSms(user.getTelephone(), "Votre code de validation : " + code);

            // 4. Retourner une réponse "En attente de 2FA"
            return ResponseEntity.ok(Map.of(
                    "status", "PENDING_2FA",
                    "message", "Code envoyé par SMS",
                    "username", user.getNom()
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects");
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2fa(@RequestBody Map<String, String> request) {
        String nom = request.get("username");
        String code = request.get("code");

        Utilisateur user = utilisateurRepository.findByNom(nom);

        if (user != null && user.getVerificationCode().equals(code)
                && user.getExpiryCode().isAfter(LocalDateTime.now())) {

            // Code valide -> On génère le vrai JWT
            var userDetails = utilisateurDetailService.loadUserByUsername(nom);
            String token = jwtUtils.generateToken(userDetails);
            String role = user.getRole().name();

            // Nettoyage du code
            user.setVerificationCode(null);
            utilisateurRepository.save(user);

            return ResponseEntity.ok(new LoginResponse(token, nom, "ROLE_" + role));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Code invalide ou expiré");
    }
}
