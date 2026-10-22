package com.bugboard26.modules.auth.controller;

import com.bugboard26.modules.auth.dto.UserResponse;
import com.bugboard26.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController 
@RequestMapping("/api/users")
@RequiredArgsConstructor 
public class UserLookupController {

    private final AuthService authService; 

    @GetMapping("/lookup")
    public ResponseEntity<List<UserResponse>> lookup(@RequestParam List<String> emails) {
        return ResponseEntity.ok(new ArrayList<>(authService.findUsersByEmails(emails).values()));
    }
    
}
