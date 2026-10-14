package com.bugboard26.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ResetPasswordRequest {
    @NotBlank (message = "La nuova password è obbligatoria")
    @Size(min = 6, message = "La nuova password deve contenere almeno 6 caratteri")
    private String newPassword; 
}
