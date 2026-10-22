package com.bugboard26.shared.exception;

public class InvalidIssueTypeForAssignmentException extends RuntimeException {
    public InvalidIssueTypeForAssignmentException() {
        super("Solo le issue di tipo BUG possono essere assegnate.");
    }
    
}
