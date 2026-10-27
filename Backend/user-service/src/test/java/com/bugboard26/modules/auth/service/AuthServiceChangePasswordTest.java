package com.bugboard26.modules.auth.service;

import com.bugboard26.common.security.JwtService;
import com.bugboard26.modules.auth.dto.ChangePasswordRequest;
import com.bugboard26.modules.auth.model.Role;
import com.bugboard26.modules.auth.model.User;
import com.bugboard26.modules.auth.repository.UserRepository;
import com.bugboard26.shared.exception.InvalidPasswordChangeException;
import com.bugboard26.shared.exception.UserNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// White-Box: AuthService.changePassword
@ExtendWith(MockitoExtension.class)
class AuthServiceChangePasswordTest {

    private static final String EMAIL = "user@bugboard26.com"; 
    private static final String CURRENT = "Attuale123"; 
    private static final String NEW = "Nuova456"; 

    @Mock
    private UserRepository userRepository; 
    @Mock
    private JwtService jwtService; 
    @Mock
    private AuthenticationManager authenticationManager; 

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 

    private AuthService authService; 

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager); 
    }

    // Cammino 1: utente inesistente 
    @Test 
    void changePassword_userNotFound_throwsNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty()); 

        assertThatThrownBy(() -> authService.changePassword(EMAIL, request(CURRENT, NEW)))
                .isInstanceOf(UserNotFoundException.class); 
        verify(userRepository, never()).save(any()); 
    }


    // Cammino 2: password attuale sbagliata 
    @Test
    void changePassword_wrongCurrentPassword_throwsInvalidChange() {
        givenUser(); 

        assertThatThrownBy(() -> authService.changePassword(EMAIL, request("Sbagliata789", NEW)))
                .isInstanceOf(InvalidPasswordChangeException.class)
                .hasMessageContaining("attuale non è corretta"); 
        verify(userRepository, never()).save(any()); 

    }

    // Cammino 3: nuova Password uguale a quella attuale
    @Test 
    void changePassword_newEqualsCurrent_throwsInvalidChange() {
        givenUser(); 

        assertThatThrownBy(() -> authService.changePassword(EMAIL, request(CURRENT, CURRENT)))
                .isInstanceOf(InvalidPasswordChangeException.class)
                .hasMessageContaining("La nuova password deve essere diversa da quella attuale"); 
        verify(userRepository, never()).save(any()); 
    }

    // Cammino 4: cambio riuscito, salvato l'hash della nuova password
    @Test 
    void changePassword_validRequest_savesNewHash() {
        givenUser(); 

        authService.changePassword(EMAIL, request(CURRENT, NEW));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class); 
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isNotEqualTo(NEW); 
        assertThat(passwordEncoder.matches(NEW, saved.getValue().getPassword())).isTrue();
        assertThat(passwordEncoder.matches(CURRENT, saved.getValue().getPassword())).isFalse();
    }

       private void givenUser() {
        User user = User.builder()
                .id("user-1")
                .nome("Mario")
                .cognome("Rossi")
                .email(EMAIL)
                .password(passwordEncoder.encode(CURRENT))
                .role(Role.USER)
                .build();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    }

    private ChangePasswordRequest request(String current, String newPassword) {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(current);
        request.setNewPassword(newPassword);
        return request;
    }

}
