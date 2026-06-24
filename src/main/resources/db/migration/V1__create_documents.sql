CREATE TABLE documents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    doc_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    author VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_documents_doc_type ON documents (doc_type);
CREATE INDEX idx_documents_status ON documents (status);
CREATE INDEX idx_documents_author ON documents (author);
CREATE INDEX idx_documents_created_at ON documents (created_at);
