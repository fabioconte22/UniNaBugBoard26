package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.issue.dto.CreateIssueRequest;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    public IssueResponse createIssue(CreateIssueRequest request, String creatorEmail) {
        Issue issue = Issue.builder()
        .titolo(request.getTitolo())
        .descrizione(request.getDescrizione())
        .type(request.getType())
        .priority(request.getPriority())
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
                issue.getType(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getCreatorEmail(),
                issue.getCreatedAt()
        );
    }

}
