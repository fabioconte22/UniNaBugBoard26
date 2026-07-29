package com.bugboard26.modules.issue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateIssueStatusRequest {
    @NotBlank(message = "Lo stato è obbligatorio!")
    private String status; 
}
