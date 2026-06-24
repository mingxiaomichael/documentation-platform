package com.company.docs.service;

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
import com.company.docs.repository.DocumentSpecifications;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DocumentService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    public DocumentService(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    @Transactional
    public UUID createDocument(CreateDocumentRequest request) {
        Document saved = documentRepository.save(documentMapper.toEntity(request));
        return saved.getId();
    }

    public DocumentResponse getDocument(UUID id) {
        return documentMapper.toResponse(findDocument(id));
    }

    @Transactional
    public DocumentResponse updateDocument(UUID id, UpdateDocumentRequest request) {
        Document document = findDocument(id);
        documentMapper.updateEntity(document, request);
        return documentMapper.toResponse(documentRepository.saveAndFlush(document));
    }

    @Transactional
    public void deleteDocument(UUID id) {
        Document document = findDocument(id);
        documentRepository.delete(document);
    }

    public Page<DocumentResponse> listDocuments(int page, int size) {
        validatePagination(page, size);
        return documentRepository.findAll(toPageable(page, size)).map(documentMapper::toResponse);
    }

    public Page<DocumentResponse> searchDocuments(
            String keyword,
            DocumentType docType,
            DocumentStatus status,
            String author,
            int page,
            int size
    ) {
        validatePagination(page, size);
        validateSearchCriteria(keyword, author);
        Page<Document> documents = documentRepository.findAll(
                DocumentSpecifications.matchesSearch(keyword, docType, status, author),
                toPageable(page, size)
        );
        return documents.map(documentMapper::toResponse);
    }

    private Document findDocument(UUID id) {
        return documentRepository.findById(id).orElseThrow(() -> new DocumentNotFoundException(id));
    }

    private Pageable toPageable(int page, int size) {
        return PageRequest.of(page, size, DEFAULT_SORT);
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new InvalidPaginationException("Page index must be greater than or equal to 0");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidPaginationException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
    }

    private void validateSearchCriteria(String keyword, String author) {
        if (keyword != null && keyword.length() > 255) {
            throw new InvalidSearchCriteriaException("Keyword must not exceed 255 characters");
        }
        if (author != null && author.length() > 255) {
            throw new InvalidSearchCriteriaException("Author filter must not exceed 255 characters");
        }
    }
}
