package com.bugboard26.modules.issue.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignIssueRequest {
    @NotBlank(message = "L'email dell'assegnatario è obbligatoria")
    @Email(message = "Formato email non valido")
    private String assigneeEmail; 
}