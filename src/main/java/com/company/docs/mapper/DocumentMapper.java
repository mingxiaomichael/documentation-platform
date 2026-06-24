package com.company.docs.mapper;

import com.company.docs.dto.CreateDocumentRequest;
import com.company.docs.dto.DocumentResponse;
import com.company.docs.dto.UpdateDocumentRequest;
import com.company.docs.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public Document toEntity(CreateDocumentRequest request) {
        Document document = new Document();
        document.setTitle(request.title());
        document.setDocType(request.docType());
        document.setContent(request.content());
        document.setStatus(request.status());
        document.setAuthor(request.author());
        return document;
    }

    public void updateEntity(Document document, UpdateDocumentRequest request) {
        document.setTitle(request.title());
        document.setDocType(request.docType());
        document.setContent(request.content());
        document.setStatus(request.status());
        document.setAuthor(request.author());
    }

    public DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDocType(),
                document.getContent(),
                document.getStatus(),
                document.getAuthor(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
