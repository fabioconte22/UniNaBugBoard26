package com.bugboard26.modules.issue.dto;

import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIssueRequest {
    @NotBlank(message = "Il titolo è obbligatorio")
    private String titolo;
    
    @NotBlank(message = "La descrizione è obbligatoria")
    private String descrizione;

    @NotNull(message = "Il tipo di issue è obbligatorio")
    private IssueType type;
    
    private IssuePriority priority;
}
