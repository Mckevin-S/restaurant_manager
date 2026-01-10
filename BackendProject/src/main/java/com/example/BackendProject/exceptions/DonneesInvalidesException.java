package com.example.BackendProject.exceptions;

/**
 * Exception levée lorsque les données fournies sont invalides
 */
public class DonneesInvalidesException extends RuntimeException {
    
    public DonneesInvalidesException(String message) {
        super(message);
    }
}
