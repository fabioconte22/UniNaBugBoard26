package com.bugboard26.modules.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nome; 

    @Column(nullable = false)
    private String cognome; 

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; 

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN || USER 

}
