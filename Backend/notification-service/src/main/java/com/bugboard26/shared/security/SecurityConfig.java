package com.bugboard26.shared.security;

import com.bugboard26.common.security.JwtAuthFilter;
import com.bugboard26.common.security.SecurityErrorHandlers;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration 
@EnableWebSecurity 
@RequiredArgsConstructor 
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; 
    private final SecurityErrorHandlers securityErrorHandlers; 

    // CSRF disattivato di proposito (regola Sonar java:S4502): l'API è senza stato e il token JWT
    // viaggia nell'intestazione Authorization, non in un cookie. Il browser non lo allega da solo
    // alle richieste partite da altri siti, quindi l'attacco CSRF qui non è possibile.
    @SuppressWarnings("java:S4502")
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(securityErrorHandlers.authenticationEntryPoint())
                .accessDeniedHandler(securityErrorHandlers.accessDeniedHandler())
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); 
        
        return http.build(); 

    }
    
}
