package com.company.docs.repository;

import com.company.docs.entity.Document;
import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class DocumentSpecifications {

    private DocumentSpecifications() {
    }

    public static Specification<Document> matchesSearch(
            String keyword,
            DocumentType docType,
            DocumentStatus status,
            String author
    ) {
        return Specification
                .where(keywordContains(keyword))
                .and(hasDocType(docType))
                .and(hasStatus(status))
                .and(hasAuthor(author));
    }

    private static Specification<Document> keywordContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), pattern)
            );
        };
    }

    private static Specification<Document> hasDocType(DocumentType docType) {
        return (root, query, criteriaBuilder) ->
                docType == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("docType"), docType);
    }

    private static Specification<Document> hasStatus(DocumentStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<Document> hasAuthor(String author) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(author)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("author")), author.trim().toLowerCase());
        };
    }
}
