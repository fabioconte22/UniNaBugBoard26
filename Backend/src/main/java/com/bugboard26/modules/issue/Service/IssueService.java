package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.issue.dto.CreateIssueRequest;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.shared.exception.IssueNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final FileStorageService fileStorageService;

    public IssueResponse createIssue(CreateIssueRequest request, String creatorEmail) {
        IssuePriority priorityToSet = request.getPriority() != null ? request.getPriority() : IssuePriority.LOW;

        Issue issue = Issue.builder()
        .titolo(request.getTitolo())
        .descrizione(request.getDescrizione())
        .type(request.getType())
        .priority(priorityToSet)
        .status(IssueStatus.TODO)
        .creatorEmail(creatorEmail)
        .build();

        Issue saved = issueRepository.save(issue);
        return toResponse(saved);
    }

    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }

    public List<IssueResponse> getMyIssues(String creatorEmail) {
        return issueRepository.findByCreatorEmail(creatorEmail)
            .stream()
            .map(this::toResponse)
            . collect(Collectors.toList());
    }

    private IssueResponse toResponse(Issue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitolo(),
                issue.getDescrizione(),
                issue.getImageUrl(),
                issue.getType(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getCreatorEmail(),
                issue.getCreatedAt()
        );
    }

    public IssueResponse uploadIssueImage(String issueId, MultipartFile file, String email) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        if(!issue.getCreatorEmail().equals(email)) {
            throw new RuntimeException("Non sei autorizzato ad allegare immagini a questa issue!");
        }

        String imageUrl = fileStorageService.uploadImage(file);

        issue.setImageUrl(imageUrl);
        Issue saved = issueRepository.save(issue);

        return toResponse(saved);

    }

}
