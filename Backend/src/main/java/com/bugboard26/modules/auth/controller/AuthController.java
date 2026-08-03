package com.bugboard26.modules.auth.controller;

import com.bugboard26.modules.auth.dto.AuthResponse;
import com.bugboard26.modules.auth.dto.LoginRequest;
import com.bugboard26.modules.auth.dto.UserResponse;
import com.bugboard26.modules.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal String email) {
        UserResponse response = authService.getCurrentUser(email);
        return ResponseEntity.ok(response);
    }

}
