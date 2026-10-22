package com.bugboard26.modules.issue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    @NotBlank(message = "Il contenuto del commento è obbligatorio")
    @Size(max = 2000, message = "Il commento non può superare i 2000 caratteri")
    private String content;
}

