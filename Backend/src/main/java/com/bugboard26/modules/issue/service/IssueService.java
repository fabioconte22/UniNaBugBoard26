package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.auth.service.AuthService;
import com.bugboard26.modules.issue.dto.CreateIssueRequest;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.model.Comment;
import com.bugboard26.modules.issue.model.CommentKind;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;
import com.bugboard26.modules.issue.repository.CommentRepository;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.shared.exception.InvalidFilterParameterException;
import com.bugboard26.shared.exception.InvalidIssueTypeForAssignmentException;
import com.bugboard26.shared.exception.IssueAccessDeniedException;
import com.bugboard26.shared.exception.IssueNotFoundException;
import com.bugboard26.shared.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final FileStorageService fileStorageService;
    private final AuthService authService;
    private final CommentRepository commentRepository;

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

    public List<IssueResponse> getMyIssues(String creatorEmail) {
        return issueRepository.findByCreatorEmail(creatorEmail)
            .stream()
            .map(this::toResponse)
            . collect(Collectors.toList());
    }

    public IssueResponse getIssueById(String issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));
        return toResponse(issue);
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
                issue.getAssigneeEmail(),
                issue.getCreatedAt()
        );
    }

    public IssueResponse uploadIssueImage(String issueId, MultipartFile file, String email) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        if(!issue.getCreatorEmail().equals(email)) {
            throw new IssueAccessDeniedException();
        }

        String imageUrl = fileStorageService.uploadImage(file);

        issue.setImageUrl(imageUrl);
        Issue saved = issueRepository.save(issue);

        return toResponse(saved);

    }

    public Page<IssueResponse> getFilteredIssues(String statusStr, String typeStr, String priorityStr, Pageable pegeable) {

        Specification<Issue> spec = Specification.where(null);

        if(statusStr != null && !statusStr.isBlank()) {
            IssueStatus status = parseEnumFilter(IssueStatus.class, statusStr, "Stato");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status)); 
        }

        if(typeStr != null && !typeStr.isBlank()) {
            IssueType type = parseEnumFilter(IssueType.class, typeStr, "Type");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));  
        }

        if(priorityStr != null && !priorityStr.isBlank()) {
            IssuePriority priority = parseEnumFilter(IssuePriority.class, priorityStr, "Priority");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }

        Page<Issue> issuesPage = issueRepository.findAll(spec, pegeable);
        return issuesPage.map(this::toResponse);
    }

    public IssueResponse assignIssue(String issueId, String assigneeEmail) {
        Issue issue = issueRepository.findById(issueId)
                        .orElseThrow(() -> new IssueNotFoundException(issueId));
        
        if(issue.getType() != IssueType.BUG) {
            throw new InvalidIssueTypeForAssignmentException();
        }

        authService.ensureUserExists(assigneeEmail);

        issue.setAssigneeEmail(assigneeEmail);
        Issue saved = issueRepository.save(issue); 

        return toResponse(saved);

    }

    public List<IssueResponse> getAssignedIssues(String assigneeEmail) {
        return issueRepository.findByAssigneeEmail(assigneeEmail)
                                .stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());

    }

    @Transactional
    public IssueResponse updateStatus(String issueId, String statusStr, String note,
                                      String callerEmail, boolean callerIsAdmin) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        IssueStatus from = issue.getStatus();
        IssueStatus to = parseEnumFilter(IssueStatus.class, statusStr, "Stato");
        String cleanNote = note == null ? "" : note.trim();

        if (from == IssueStatus.TODO && to == IssueStatus.IN_PROGRESS) {
            if (issue.getType() != IssueType.BUG && issue.getAssigneeEmail() == null) {
                // Presa in carico: per le issue non-bug, chi la avvia ne diventa responsabile.
                issue.setAssigneeEmail(callerEmail);
            } else {
                requireAssignee(issue, callerEmail);
            }
        } else if (from == IssueStatus.IN_PROGRESS && to == IssueStatus.RESOLVED) {
            requireAssignee(issue, callerEmail);
            if (cleanNote.isEmpty()) {
                throw new InvalidStatusTransitionException(
                        "Per segnare la issue come risolta serve un breve report di quello che è stato fatto.");
            }
        } else if (from == IssueStatus.RESOLVED
                && (to == IssueStatus.CLOSED || to == IssueStatus.IN_PROGRESS)) {
            if (!callerIsAdmin) {
                throw new IssueAccessDeniedException();
            }
        } else {
            throw new InvalidStatusTransitionException(
                    "Passaggio di stato non consentito: da " + from + " a " + to + ".");
        }

        issue.setStatus(to);
        Issue saved = issueRepository.save(issue);

        commentRepository.save(Comment.builder()
                .kind(CommentKind.STATUS_CHANGE)
                .fromStatus(from)
                .toStatus(to)
                .content(cleanNote)
                .authorEmail(callerEmail)
                .issue(saved)
                .build());

        return toResponse(saved);
    }

    private void requireAssignee(Issue issue, String callerEmail) {
        if (!callerEmail.equals(issue.getAssigneeEmail())) {
            throw new IssueAccessDeniedException();
        }
    }

    private <T extends Enum<T>> T parseEnumFilter(Class<T> enumType, String value, String fieldName) {
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new InvalidFilterParameterException("Il valore specificato per '" + fieldName + "' non è valido: " + value);
        }
    }

}
