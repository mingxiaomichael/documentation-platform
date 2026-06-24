package com.company.docs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.company.docs.dto.CreateDocumentRequest;
import com.company.docs.dto.DocumentResponse;
import com.company.docs.dto.UpdateDocumentRequest;
import com.company.docs.entity.Document;
import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import com.company.docs.exception.DocumentNotFoundException;
import com.company.docs.exception.InvalidPaginationException;
import com.company.docs.exception.InvalidSearchCriteriaException;
import com.company.docs.mapper.DocumentMapper;
import com.company.docs.repository.DocumentRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void createsDocumentAndReturnsId() {
        UUID id = UUID.randomUUID();
        CreateDocumentRequest request = new CreateDocumentRequest(
                "Redis Notes",
                DocumentType.DEVELOPMENT,
                "Use PostgreSQL only in V1",
                DocumentStatus.DRAFT,
                "Michael"
        );
        Document unsaved = new Document();
        Document saved = document(id);

        when(documentMapper.toEntity(request)).thenReturn(unsaved);
        when(documentRepository.save(unsaved)).thenReturn(saved);

        UUID result = documentService.createDocument(request);

        assertThat(result).isEqualTo(id);
    }

    @Test
    void throwsWhenDocumentIsMissing() {
        UUID id = UUID.randomUUID();
        when(documentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocument(id))
                .isInstanceOf(DocumentNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void deletesExistingDocument() {
        UUID id = UUID.randomUUID();
        Document document = document(id);
        when(documentRepository.findById(id)).thenReturn(Optional.of(document));

        documentService.deleteDocument(id);

        verify(documentRepository).delete(document);
    }

    @Test
    void updatesDocumentAfterFlushingTimestampCallbacks() {
        UUID id = UUID.randomUUID();
        Document document = document(id);
        UpdateDocumentRequest request = new UpdateDocumentRequest(
                "Updated Redis Notes",
                DocumentType.DEVELOPMENT,
                "Updated content",
                DocumentStatus.REVIEW,
                "Michael"
        );
        DocumentResponse response = new DocumentResponse(
                id,
                "Updated Redis Notes",
                DocumentType.DEVELOPMENT,
                "Updated content",
                DocumentStatus.REVIEW,
                "Michael",
                document.getCreatedAt(),
                LocalDateTime.now()
        );

        when(documentRepository.findById(id)).thenReturn(Optional.of(document));
        when(documentRepository.saveAndFlush(document)).thenReturn(document);
        when(documentMapper.toResponse(document)).thenReturn(response);

        DocumentResponse result = documentService.updateDocument(id, request);

        assertThat(result).isEqualTo(response);
        verify(documentMapper).updateEntity(document, request);
        verify(documentRepository).saveAndFlush(document);
    }

    @Test
    void rejectsNegativePage() {
        assertThatThrownBy(() -> documentService.listDocuments(-1, 20))
                .isInstanceOf(InvalidPaginationException.class)
                .hasMessage("Page index must be greater than or equal to 0");

        verifyNoInteractions(documentRepository);
    }

    @Test
    void rejectsPageSizeAboveLimit() {
        assertThatThrownBy(() -> documentService.listDocuments(0, 101))
                .isInstanceOf(InvalidPaginationException.class)
                .hasMessage("Page size must be between 1 and 100");

        verifyNoInteractions(documentRepository);
    }

    @Test
    void rejectsSearchKeywordAboveLimit() {
        String keyword = "a".repeat(256);

        assertThatThrownBy(() -> documentService.searchDocuments(keyword, null, null, null, 0, 20))
                .isInstanceOf(InvalidSearchCriteriaException.class)
                .hasMessage("Keyword must not exceed 255 characters");

        verifyNoInteractions(documentRepository);
    }

    private Document document(UUID id) {
        Document document = new Document();
        document.setId(id);
        document.setTitle("Redis Notes");
        document.setDocType(DocumentType.DEVELOPMENT);
        document.setContent("Content");
        document.setStatus(DocumentStatus.DRAFT);
        document.setAuthor("Michael");
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        return document;
    }
}
