package com.bugboard26.modules.auth.config;

import com.bugboard26.modules.auth.model.Role;
import com.bugboard26.modules.auth.model.User;
import com.bugboard26.modules.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email}")
    private String defaultAdminEmail;

    @Value("${app.admin.default-password}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(defaultAdminEmail).isPresent()) {
            return;
        }

        User admin = User.builder()
                .nome("Admin")
                .cognome("BugBoard26")
                .email(defaultAdminEmail)
                .password(passwordEncoder.encode(defaultAdminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
    }
}
