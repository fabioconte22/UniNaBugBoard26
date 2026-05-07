package com.bugboard26.shared.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("Utente con email: " + email + " non trovato!");
    }
    
}
