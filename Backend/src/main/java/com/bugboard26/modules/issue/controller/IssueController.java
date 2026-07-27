package com.bugboard26.modules.issue.controller;

import com.bugboard26.modules.issue.dto.CreateIssueRequest;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.service.IssueService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;


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

    @GetMapping("/me")
    public ResponseEntity<List<IssueResponse>> getMyIssues(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(issueService.getMyIssues(email));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<IssueResponse> uploadImage(
        @PathVariable String id,
        @RequestParam("file") MultipartFile file, 
        @AuthenticationPrincipal String email) {

            IssueResponse response = issueService.uploadIssueImage(id, file, email);
            return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<IssueResponse>> getAllIssues(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priority,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

                Page<IssueResponse> issues = issueService.getFilteredIssues(status, type, priority, pageable);
                return ResponseEntity.ok(issues);
    }



}
