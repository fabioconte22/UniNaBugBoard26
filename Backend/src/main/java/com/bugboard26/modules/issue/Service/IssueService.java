package com.bugboard26.modules.issue.Service;

import com.bugboard26.modules.issue.dto.CreateIssueRequest;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.Repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    pubilc IssueResponse createIssue(CreateIssueRequest request, String creatoreEmail) {
        Issue issue = Issue.builder()
        .titolo(request.getTitolo())
        .descrizione(request.getDescrizione())
        .type(request.getType())
        .priority(request.getPriority())
        .status(IssueStatus.TODO)
        .creatoreEmail(creatoreEmail)
        .build();

        Issue saved = issueRepository.save(issue);
        return toResponse(saved);
    }

    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }

    public List<IssueResponse> getMyIssues(String creatoreEmail) {
        return issueRepository.findByCreatoreEmail(
        .stream()
        .map(this::toResponse))
        .collect(Collectors.toList());
    }

    private IssueResponse toResponse(Issue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitolo(),
                issue.getDescrizione(),
                issue.getType(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getCreatoreEmail(),
                issue.getCreatedAt()
        );
    }

}
