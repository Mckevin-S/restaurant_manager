package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.LoginResponse;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.security.JwtUtils;
import com.example.BackendProject.services.implementations.SmsService;
import com.example.BackendProject.services.implementations.UtilisateurDetailService;
import com.example.BackendProject.utils.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
    public ResponseEntity<?> login(@RequestBody UtilisateurDto request, HttpServletRequest httpRequest) {
        String logContext = LoggingUtils.getLogContext(httpRequest);
        System.out.println("Tentative de connexion (Email) : " + request.getEmail());
        logger.info("{} Tentative de login pour l'email: {}", logContext, request.getEmail());

        try {
            // 1. Authentification via Email
            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getMotDePasse()
            ));

            // 2. On récupère l'utilisateur pour obtenir son NOM
            Utilisateur user = utilisateurRepository.findByEmail(request.getEmail());

            if (user == null) {
                logger.warn("{} Utilisateur non trouvé après authentification pour: {}", logContext, request.getEmail());
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
            logger.info("{} Code 2FA généré et envoyé par SMS à {} pour l'utilisateur {}", logContext, user.getTelephone(), user.getNom());

            return ResponseEntity.ok(Map.of(
                    "status", "PENDING_2FA",
                    "message", "Code envoyé par SMS",
                    "username", user.getNom() // Le front utilisera ce nom pour l'étape suivante
            ));

        } catch (BadCredentialsException e) {
            logger.error("{} Échec d'authentification (Bad Credentials) pour: {}", logContext, request.getEmail());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
        } catch (Exception e) {
            logger.error("{} Erreur serveur lors du login: {}", logContext, e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2fa(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String logContext = LoggingUtils.getLogContext(httpRequest);
        String nom = request.get("username"); // On récupère bien le nom ici
        String code = request.get("code");

        logger.info("{} Tentative de vérification 2FA pour l'utilisateur: {}", logContext, nom);

        if (!pendingVerifications.containsKey(nom)) {
            logger.warn("{} Tentative de vérification 2FA échouée: Session inexistante pour {}", logContext, nom);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expirée ou nom invalide");
        }

        Utilisateur user = utilisateurRepository.findByNom(nom);

        if (user != null && code.equals(user.getVerificationCode())
                && user.getExpiryCode().isAfter(LocalDateTime.now())) {

            // Succès -> Le JWT est généré
            var userDetails = utilisateurDetailService.loadUserByUsername(user.getEmail());
            String token = jwtUtils.generateToken(userDetails);

            // Nettoyage
            user.setVerificationCode(null);
            user.setExpiryCode(null);
            utilisateurRepository.save(user);
            pendingVerifications.remove(nom);

            logger.info("{}  Vérification 2FA réussie pour {}", logContext, nom);

            return ResponseEntity.ok(new LoginResponse(token, user.getNom(), "ROLE_" + user.getRole().name()));
        }

        logger.warn("{} Code 2FA incorrect ou expiré pour {}", logContext, nom);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Code incorrect ou expiré");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String logContext = LoggingUtils.getLogContext(httpRequest);
        String nom = request.get("username");

        // 1. Nettoyer les vérifications 2FA en cours si elles existent
        if (nom != null) {
            pendingVerifications.remove(nom);
        }

        // 2. Vider le contexte de sécurité de Spring
        SecurityContextHolder.clearContext();

        System.out.println("Déconnexion réussie pour : " + nom);
        logger.info("{} Déconnexion réussie pour l'utilisateur: {}", logContext, nom);

        return ResponseEntity.ok(Map.of(
                "message", "Déconnexion réussie. N'oubliez pas de supprimer le token côté client."
        ));
    }
}