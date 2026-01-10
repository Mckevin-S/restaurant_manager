package com.example.BackendProject.exceptions;

/**
 * Exception levée lorsqu'une opération n'est pas autorisée
 */
public class OperationNonAutoriseeException extends RuntimeException {
    
    public OperationNonAutoriseeException(String message) {
        super(message);
    }
}
