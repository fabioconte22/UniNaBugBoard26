package com.bugboard26;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BugBoard26Application {

    public static void main(String[] args) {
        SpringApplication.run(BugBoard26Application.class, args);
    }

}