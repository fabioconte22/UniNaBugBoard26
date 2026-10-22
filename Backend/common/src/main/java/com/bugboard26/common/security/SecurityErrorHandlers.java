package com.bugboard26.common.security;

import com.bugboard26.common.web.ApiErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SecurityErrorHandlers {
    
    private final ObjectMapper objectMapper;
    
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) ->
                write(response, HttpStatus.UNAUTHORIZED, "Autenticazione richiesta: effettua di nuovo l'accesso."); 
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, authException) ->
                write(response, HttpStatus.FORBIDDEN, "Non hai i permessi per questa operazione."); 
    }

    private void write(HttpServletResponse response,  HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), ApiErrors.body(status, message)); 
    }
}
