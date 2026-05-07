package com.bugboard26.modules.auth.repository;

import com.bugboard26.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByEmail(String email);
    
} 
