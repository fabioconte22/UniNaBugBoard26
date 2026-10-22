package com.bugboard26.modules.issue.dto;

import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIssueRequest {
    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(max = 255, message = "Il titolo non può superare i 255 caratteri")
    private String titolo;
    
    @NotBlank(message = "La descrizione è obbligatoria")
    @Size (max = 2000, message = "La descrizione non può superare i 2000 caratteri")
    private String descrizione;

    @NotNull(message = "Il tipo di issue è obbligatorio")
    private IssueType type;
    
    private IssuePriority priority;
}
