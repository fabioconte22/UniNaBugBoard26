package com.bugboard26.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiErrors {

    private ApiErrors() {
    }

    public static Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>(); 
        body.put("timestamp", Instant.now().toString()); 
        body.put("status", status.value()); 
        body.put("error", status.getReasonPhrase()); 
        body.put("message", message); 
    }

    public static ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {

        return ResponseEntity.status(status).body(body(status, message)); 

    }

    public static ResponseEntity<Map<String, Object>> response(HttpStatus status, String message, Map<String, String> details) {

        Map<String, Object> body = body(status, message); 
        body.put("details", details); 
        return ResponseEntity.status(status).body(body);
    }
    
}
