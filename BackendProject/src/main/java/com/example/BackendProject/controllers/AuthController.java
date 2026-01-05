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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    // On continue d'utiliser le nom comme clé pour la vérification
    private final Map<String, String> pendingVerifications = new ConcurrentHashMap<>();

    private final AuthenticationManager authManager;
    private final UtilisateurDetailService utilisateurDetailService;
    private final JwtUtils jwtUtils;
    private final SmsService smsService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(AuthenticationManager authManager,
                          UtilisateurDetailService utilisateurDetailService,
                          JwtUtils jwtUtils,
                          SmsService smsService,
                          UtilisateurRepository utilisateurRepository) {
        this.authManager = authManager;
        this.utilisateurDetailService = utilisateurDetailService;
        this.jwtUtils = jwtUtils;
        this.smsService = smsService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UtilisateurDto request) {
        System.out.println("Tentative de connexion (Email) : " + request.getEmail());

        try {
            // 1. Authentification via Email
            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getMotDePasse()
            ));

            // 2. On récupère l'utilisateur pour obtenir son NOM
            Utilisateur user = utilisateurRepository.findByEmail(request.getEmail());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Compte introuvable");
            }

            // 3. Génération du code
            String code = String.valueOf((int)(Math.random() * 9000) + 1000);
            user.setVerificationCode(code);
            user.setExpiryCode(LocalDateTime.now().plusMinutes(5));
            utilisateurRepository.save(user);

            // 4. Stockage par NOM (comme vous le souhaitiez)
            pendingVerifications.put(user.getNom(), code);

            // 5. Envoyer SMS
            smsService.sendSms(user.getTelephone(), "Code de validation : " + code);

            System.out.println("✅ Code généré pour le nom [" + user.getNom() + "] : " + code);

            return ResponseEntity.ok(Map.of(
                    "status", "PENDING_2FA",
                    "message", "Code envoyé par SMS",
                    "username", user.getNom() // Le front utilisera ce nom pour l'étape suivante
            ));

        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2fa(@RequestBody Map<String, String> request) {
        String nom = request.get("username"); // On récupère bien le nom ici
        String code = request.get("code");

        if (!pendingVerifications.containsKey(nom)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expirée ou nom invalide");
        }

        Utilisateur user = utilisateurRepository.findByNom(nom);

        if (user != null && code.equals(user.getVerificationCode())
                && user.getExpiryCode().isAfter(LocalDateTime.now())) {

            // Succès -> Le JWT est généré (basé sur l'email ou le nom selon votre JwtUtils)
            var userDetails = utilisateurDetailService.loadUserByUsername(user.getEmail());
            String token = jwtUtils.generateToken(userDetails);

            // Nettoyage
            user.setVerificationCode(null);
            user.setExpiryCode(null);
            utilisateurRepository.save(user);
            pendingVerifications.remove(nom);

            return ResponseEntity.ok(new LoginResponse(token, user.getNom(), "ROLE_" + user.getRole().name()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Code incorrect ou expiré");
    }
}