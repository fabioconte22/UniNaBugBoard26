package com.bugboard26.modules.auth.dto;

import com.bugboard26.modules.auth.model.Role;

public record UserResponse(String id, String nome, String cognome, String email, Role role) {
}
