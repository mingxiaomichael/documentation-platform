package com.company.docs.controller;

import com.company.docs.dto.CreateDocumentRequest;
import com.company.docs.dto.DocumentIdResponse;
import com.company.docs.dto.DocumentResponse;
import com.company.docs.dto.UpdateDocumentRequest;
import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import com.company.docs.service.DocumentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<DocumentIdResponse> createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        UUID id = documentService.createDocument(request);
        return ResponseEntity.created(URI.create("/api/documents/" + id)).body(new DocumentIdResponse(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocument(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDocumentRequest request
    ) {
        return ResponseEntity.ok(documentService.updateDocument(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> listDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return toPageResponse(documentService.listDocuments(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentResponse>> searchDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) DocumentType docType,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return toPageResponse(documentService.searchDocuments(keyword, docType, status, author, page, size));
    }

    private ResponseEntity<Page<DocumentResponse>> toPageResponse(Page<DocumentResponse> documents) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(documents.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(documents.getTotalPages()));
        headers.add("X-Page-Number", String.valueOf(documents.getNumber()));
        headers.add("X-Page-Size", String.valueOf(documents.getSize()));
        return ResponseEntity.ok().headers(headers).body(documents);
    }
}
