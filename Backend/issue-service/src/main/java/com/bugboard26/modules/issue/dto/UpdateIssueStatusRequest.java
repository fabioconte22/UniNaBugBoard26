package com.bugboard26.modules.issue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateIssueStatusRequest {
    @NotBlank(message = "Lo stato è obbligatorio")
    private String status;

    @Size(max = 2000, message = "La nota non può superare i 2000 caratteri")
    private String note;
    }
