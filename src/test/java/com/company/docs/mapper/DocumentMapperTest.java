package com.company.docs.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.docs.dto.CreateDocumentRequest;
import com.company.docs.dto.UpdateDocumentRequest;
import com.company.docs.entity.Document;
import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import org.junit.jupiter.api.Test;

class DocumentMapperTest {

    private final DocumentMapper mapper = new DocumentMapper();

    @Test
    void mapsCreateRequestToEntity() {
        CreateDocumentRequest request = new CreateDocumentRequest(
                "Payment Service Deployment",
                DocumentType.DEPLOYMENT,
                "Deployment instructions",
                DocumentStatus.DRAFT,
                "Michael"
        );

        Document document = mapper.toEntity(request);

        assertThat(document.getTitle()).isEqualTo("Payment Service Deployment");
        assertThat(document.getDocType()).isEqualTo(DocumentType.DEPLOYMENT);
        assertThat(document.getContent()).isEqualTo("Deployment instructions");
        assertThat(document.getStatus()).isEqualTo(DocumentStatus.DRAFT);
        assertThat(document.getAuthor()).isEqualTo("Michael");
    }

    @Test
    void updatesEntityFromRequest() {
        Document document = new Document();
        UpdateDocumentRequest request = new UpdateDocumentRequest(
                "Updated",
                DocumentType.TEST,
                "Updated content",
                DocumentStatus.REVIEW,
                "Dana"
        );

        mapper.updateEntity(document, request);

        assertThat(document.getTitle()).isEqualTo("Updated");
        assertThat(document.getDocType()).isEqualTo(DocumentType.TEST);
        assertThat(document.getStatus()).isEqualTo(DocumentStatus.REVIEW);
        assertThat(document.getAuthor()).isEqualTo("Dana");
    }
}
