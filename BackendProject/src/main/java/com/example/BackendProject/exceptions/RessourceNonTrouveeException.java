package com.example.BackendProject.exceptions;

/**
 * Exception levée lorsqu'une ressource demandée n'est pas trouvée en base de données
 */
public class RessourceNonTrouveeException extends RuntimeException {
    
    public RessourceNonTrouveeException(String message) {
        super(message);
    }
    
    public RessourceNonTrouveeException(String ressource, String champ, Object valeur) {
        super(String.format("%s non trouvé(e) avec %s : '%s'", ressource, champ, valeur));
    }
}
