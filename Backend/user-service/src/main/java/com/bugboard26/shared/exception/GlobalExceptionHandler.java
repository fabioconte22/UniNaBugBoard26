package com.bugboard26.shared.exception;

import com.bugboard26.common.web.ApiErrors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice 
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ApiErrors.response(HttpStatus.CONFLICT, ex.getMessage()); 
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return ApiErrors.response(HttpStatus.NOT_FOUND, ex.getMessage()); 
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredential(InvalidCredentialException ex) {
        return ApiErrors.response(HttpStatus.UNAUTHORIZED, ex.getMessage()); 
    }

    @ExceptionHandler(InvalidPasswordChangeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPasswordChange(InvalidPasswordChangeException ex) {
        return ApiErrors.response(HttpStatus.BAD_REQUEST, ex.getMessage()); 
    }
}
