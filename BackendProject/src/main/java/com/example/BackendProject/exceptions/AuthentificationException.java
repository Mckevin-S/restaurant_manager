package com.example.BackendProject.exceptions;

/**
 * Exception levée lors d'un problème d'authentification
 */
public class AuthentificationException extends RuntimeException {
    
    public AuthentificationException(String message) {
        super(message);
    }
}
