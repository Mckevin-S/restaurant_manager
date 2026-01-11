package com.example.BackendProject.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les réponses d'erreur standardisées
 */
@Getter
@Setter
public class ReponseErreur {
    
    private LocalDateTime horodatage;
    private int statut;
    private String erreur;
    private String message;
    private String chemin;
    private List<String> details;

    public ReponseErreur() {
    }
    
    public ReponseErreur(LocalDateTime horodatage, int statut, String erreur, String message, String chemin) {
        this.horodatage = horodatage;
        this.statut = statut;
        this.erreur = erreur;
        this.message = message;
        this.chemin = chemin;
    }

    public ReponseErreur(LocalDateTime horodatage, int statut, String erreur, String message, String chemin, List<String> details) {
        this.horodatage = horodatage;
        this.statut = statut;
        this.erreur = erreur;
        this.message = message;
        this.chemin = chemin;
        this.details = details;
    }
}
