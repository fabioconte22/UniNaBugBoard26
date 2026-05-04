package com.bugboard26.modules.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    
}
