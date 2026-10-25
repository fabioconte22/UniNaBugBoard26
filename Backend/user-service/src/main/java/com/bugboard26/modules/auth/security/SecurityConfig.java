package com.bugboard26.modules.auth.security;

import com.bugboard26.common.security.JwtAuthFilter;
import com.bugboard26.common.security.SecurityErrorHandlers;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration 
@EnableWebSecurity 
@RequiredArgsConstructor 
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; 
    private final UserDetailsService userDetailsService; 
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
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(securityErrorHandlers.authenticationEntryPoint())
                .accessDeniedHandler(securityErrorHandlers.accessDeniedHandler())
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build(); 
    }

    @Bean 
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); 
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider; 
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean 
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); 
    }
}