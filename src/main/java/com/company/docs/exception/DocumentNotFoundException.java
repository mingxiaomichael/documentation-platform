package com.company.docs.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class DocumentNotFoundException extends ApiException {

    public DocumentNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found: " + id);
    }
}
