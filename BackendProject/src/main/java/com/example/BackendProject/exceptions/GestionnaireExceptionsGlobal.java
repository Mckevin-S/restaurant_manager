package com.example.BackendProject.exceptions;

import com.example.BackendProject.dto.ReponseErreur;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire global des exceptions pour l'application
 * Intercepte toutes les exceptions et retourne des réponses standardisées
 */
@RestControllerAdvice
public class GestionnaireExceptionsGlobal {
    
    private static final Logger logger = LoggerFactory.getLogger(GestionnaireExceptionsGlobal.class);
    
    /**
     * Gère les exceptions de ressource non trouvée (404)
     */
    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<ReponseErreur> gererRessourceNonTrouvee(
            RessourceNonTrouveeException ex, 
            HttpServletRequest request) {
        
        logger.warn("Ressource non trouvée : {} - Chemin : {}", ex.getMessage(), request.getRequestURI());
        
        ReponseErreur erreur = new ReponseErreur(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Ressource Non Trouvée",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(erreur, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Gère les exceptions de validation des données (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ReponseErreur> gererValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + " : " + error.getDefaultMessage());
        }
        
        logger.warn("Erreur de validation : {} - Chemin : {}", details, request.getRequestURI());
        
        ReponseErreur erreur = new ReponseErreur(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Données Invalides",
            "Les données fournies ne sont pas valides",
            request.getRequestURI(),
            details
        );
        
        return new ResponseEntity<>(erreur, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Gère les exceptions de données invalides (400)
     */
    @ExceptionHandler(DonneesInvalidesException.class)
    public ResponseEntity<ReponseErreur> gererDonneesInvalides(
            DonneesInvalidesException ex,
            HttpServletRequest request) {
        
        logger.warn("Données invalides : {} - Chemin : {}", ex.getMessage(), request.getRequestURI());
        
        ReponseErreur erreur = new ReponseErreur(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Données Invalides",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(erreur, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Gère les exceptions d'authentification (401)
     */
    @ExceptionHandler({AuthentificationException.class, BadCredentialsException.class})
    public ResponseEntity<ReponseErreur> gererAuthentificationException(
            Exception ex,
            HttpServletRequest request) {
        
        logger.warn("Échec d'authentification : {} - Chemin : {}", ex.getMessage(), request.getRequestURI());
        
        ReponseErreur erreur = new ReponseErreur(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "Authentification Échouée",
            "Email ou mot de passe incorrect",
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(erreur, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Gère les exceptions d'accès refusé (403)
     */
    @ExceptionHandler({OperationNonAutoriseeException.class, AccessDeniedException.class})
    public ResponseEntity<ReponseErreur> gererAccesRefuse(
            Exception ex,
            HttpServletRequest request) {
        
        logger.warn("Accès refusé : {} - Chemin : {}", ex.getMessage(), request.getRequestURI());
        
        ReponseErreur erreur = new ReponseErreur(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Accès Refusé",
            "Vous n'avez pas les permissions nécessaires pour cette opération",
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(erreur, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Gère les exceptions de fichier trop volumineux (413)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ReponseErreur> gererFichierTropVolumineux(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {
        
        logger.warn("Fichier trop volumineux - Chemin : {}", request.getRequestURI());
        
        ReponseErreur erreur = new ReponseErreur(
            LocalDateTime.now(),
            HttpStatus.PAYLOAD_TOO_LARGE.value(),
            "Fichier Trop Volumineux",
            "La taille du fichier dépasse la limite autorisée (5 MB)",
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(erreur, HttpStatus.PAYLOAD_TOO_LARGE);
    }
    
    /**
     * Gère toutes les autres exceptions non prévues (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReponseErreur> gererExceptionGenerale(
            Exception ex,
            HttpServletRequest request) {
        
        logger.error("Erreur serveur inattendue : {} - Chemin : {}", ex.getMessage(), request.getRequestURI(), ex);
        
        ReponseErreur erreur = new ReponseErreur(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erreur Serveur",
            "Une erreur inattendue s'est produite. Veuillez réessayer plus tard.",
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(erreur, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
