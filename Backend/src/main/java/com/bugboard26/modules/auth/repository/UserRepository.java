package com.bugboard26.modules.auth.repository;

import com.bugboard26.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Collection;
import java.util.List;


public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByEmail(String email);
    List<User> findByEmailIn(Collection<String> emails);
    
} 
