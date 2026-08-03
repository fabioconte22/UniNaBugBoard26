package com.bugboard26.shared.exception;

public class IssueAccessDeniedException extends RuntimeException{
    public IssueAccessDeniedException() {
        super("Non sei autorizzato a compiere operazioni su questa issue!");
    }
}
