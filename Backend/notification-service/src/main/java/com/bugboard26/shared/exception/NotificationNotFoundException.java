package com.bugboard26.shared.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException() {
        super("Notifica non trovata!"); 
    }
}
