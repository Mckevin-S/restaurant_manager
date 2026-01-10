package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les réponses d'erreur standardisées
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponseErreur {
    
    private LocalDateTime horodatage;
    private int statut;
    private String erreur;
    private String message;
    private String chemin;
    private List<String> details;
    
    public ReponseErreur(LocalDateTime horodatage, int statut, String erreur, String message, String chemin) {
        this.horodatage = horodatage;
        this.statut = statut;
        this.erreur = erreur;
        this.message = message;
        this.chemin = chemin;
    }
}
