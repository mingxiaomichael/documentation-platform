package com.company.docs.graphql;

import com.company.docs.dto.CreateDocumentRequest;
import com.company.docs.dto.DocumentResponse;
import com.company.docs.dto.UpdateDocumentRequest;
import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import com.company.docs.service.DocumentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class DocumentGraphqlController {

    private final DocumentService documentService;

    public DocumentGraphqlController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @QueryMapping
    public DocumentResponse document(@Argument UUID id) {
        return documentService.getDocument(id);
    }

    @QueryMapping
    public List<DocumentResponse> documents(
            @Argument String keyword,
            @Argument DocumentType docType,
            @Argument DocumentStatus status,
            @Argument String author,
            @Argument Integer page,
            @Argument Integer size
    ) {
        Page<DocumentResponse> documents = documentService.searchDocuments(
                keyword,
                docType,
                status,
                author,
                page == null ? 0 : page,
                size == null ? 20 : size
        );
        return documents.getContent();
    }

    @MutationMapping
    public UUID createDocument(@Valid @Argument("input") CreateDocumentRequest request) {
        return documentService.createDocument(request);
    }

    @MutationMapping
    public DocumentResponse updateDocument(@Argument UUID id, @Valid @Argument("input") UpdateDocumentRequest request) {
        return documentService.updateDocument(id, request);
    }

    @MutationMapping
    public Boolean deleteDocument(@Argument UUID id) {
        documentService.deleteDocument(id);
        return true;
    }
}
