package com.company.docs.dto;

import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String title,
        DocumentType docType,
        String content,
        DocumentStatus status,
        String author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
