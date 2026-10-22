package com.bugboard26.shared.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException() {
        super("Credenziali non valide!");
    }
    
}
