package com.company.docs.dto;

import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDocumentRequest(
        @NotBlank @Size(max = 255) String title,
        @NotNull DocumentType docType,
        @NotBlank String content,
        @NotNull DocumentStatus status,
        @NotBlank @Size(max = 255) String author
) {
}
