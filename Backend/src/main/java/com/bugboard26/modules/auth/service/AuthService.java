package com.bugboard26.modules.auth.service;

import com.bugboard26.modules.auth.dto.AuthResponse;
import com.bugboard26.modules.auth.dto.CreateUserRequest;
import com.bugboard26.modules.auth.dto.LoginRequest;
import com.bugboard26.modules.auth.dto.UserResponse;
import com.bugboard26.modules.auth.model.User;
import com.bugboard26.modules.auth.repository.UserRepository;
import com.bugboard26.shared.exception.EmailAlreadyExistsException;
import com.bugboard26.shared.exception.InvalidCredentialException;
import com.bugboard26.shared.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponse createUser(CreateUserRequest request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
        .nome(request.getNome())
        .cognome(request.getCognome())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();

        User saved = userRepository.save(user);

        return toResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialException();
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));
        
        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole()));    
    
    }

    public void ensureUserExists(String email) {
        if(userRepository.findByEmail(email).isEmpty()) {
            throw new UserNotFoundException(email);
        }
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getNome(), user.getCognome(), user.getEmail(), user.getRole());
    }
    
}
