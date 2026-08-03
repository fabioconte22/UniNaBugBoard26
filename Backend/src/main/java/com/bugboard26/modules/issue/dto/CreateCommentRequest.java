package com.bugboard26.modules.issue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    @NotBlank(message = "Il contenuto del commento è obbligatorio")
    private String content;
}

