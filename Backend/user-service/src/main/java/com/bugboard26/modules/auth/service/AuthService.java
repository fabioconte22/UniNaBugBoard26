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
import com.bugboard26.modules.auth.dto.ChangePasswordRequest;
import com.bugboard26.modules.auth.dto.ResetPasswordRequest;
import com.bugboard26.shared.exception.InvalidPasswordChangeException;
import com.bugboard26.common.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


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
        
        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole().name()));    
    
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

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll(Sort.by("cognome", "nome"))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public Map<String, UserResponse> findUsersByEmails(Collection<String> emails) {
        return userRepository.findByEmailIn(emails)
                .stream()
                .collect(Collectors.toMap(User::getEmail, this::toResponse));
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordChangeException("La password attuale non è corretta.");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())){
            throw new InvalidPasswordChangeException("La nuova password deve essere diversa da quella attuale.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user); 
    }

    public void resetPassword(String userId, ResetPasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user); 
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getNome(), user.getCognome(), user.getEmail(), user.getRole());
    }
    
}
