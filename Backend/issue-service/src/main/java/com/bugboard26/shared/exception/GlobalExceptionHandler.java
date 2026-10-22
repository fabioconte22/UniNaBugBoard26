package com.bugboard26.shared.exception;

import com.bugboard26.common.web.ApiErrors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return ApiErrors.response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IssueNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleIssueNotFound(IssueNotFoundException ex){
        return ApiErrors.response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationNotFound(NotificationNotFoundException ex) {
        return ApiErrors.response(HttpStatus.NOT_FOUND, ex.getMessage()); 
    }

    @ExceptionHandler(IssueAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleIssueAccessDenied(IssueAccessDeniedException ex) {
        return ApiErrors.response(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InvalidFilterParameterException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFilterParameter(InvalidFilterParameterException ex) {
      return ApiErrors.response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidIssueTypeForAssignmentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidIssueTypeForAssignment(InvalidIssueTypeForAssignmentException ex) {
        return ApiErrors.response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        return ApiErrors.response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidImage(InvalidImageException ex) {
        return ApiErrors.response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return ApiErrors.response(HttpStatus.PAYLOAD_TOO_LARGE, "L'immagine supera il limite di 1 MB.");
    }
}
