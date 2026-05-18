package com.bugboard26.shared.exception;

public class IssueNotFoundException extends RuntimeException {
    public IssueNotFoundException(String id){
        super("Issue con id: " + id + " non trovata!");
    }
    
}
