package com.bugboard26.shared.exception; 

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email già registrata: " + email); 
    }
}