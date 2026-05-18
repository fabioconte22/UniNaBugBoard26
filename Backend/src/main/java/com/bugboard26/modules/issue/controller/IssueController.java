package com.bugboard26.modules.issue.controller;

import com.bugboard26.modules.issue.dto.CreateIssueRequest;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.service.IssueService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issue")
@RequiredArgsConstructor
public class IssueController {
    
    private final IssueService issueService; 

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @Valid @RequestBody CreateIssueRequest request,
            @AuthenticationPrincipal String email ) {
        IssueResponse response = issueService.createIssue(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues() {
            return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/me")
    public ResponseEntity<List<IssueResponse>> getMyIssues(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(issueService.getMyIssues(email));
    }

}
