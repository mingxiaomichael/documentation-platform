package com.company.docs.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.company.docs.dto.DocumentResponse;
import com.company.docs.dto.UpdateDocumentRequest;
import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import com.company.docs.exception.GlobalExceptionHandler;
import com.company.docs.service.DocumentService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = "spring.jackson.mapper.accept-case-insensitive-enums=true")
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Test
    void returnsMethodNotAllowedWhenPostingToDocumentId() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/api/documents/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson("Draft")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", containsString("PUT")))
                .andExpect(jsonPath("$.code", is("METHOD_NOT_ALLOWED")));
    }

    @Test
    void updatesDocumentWithCaseInsensitiveEnumValues() throws Exception {
        UUID id = UUID.randomUUID();
        DocumentResponse response = new DocumentResponse(
                id,
                "The Second Document",
                DocumentType.TEST,
                "Some features need to be completed!",
                DocumentStatus.DRAFT,
                "Michael",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(documentService.updateDocument(eq(id), any(UpdateDocumentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/documents/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson("Draft")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.status", is("DRAFT")));
    }

    private String updateJson(String status) {
        return """
                {
                  "title": "The Second Document",
                  "docType": "TEST",
                  "content": "Some features need to be completed!",
                  "status": "%s",
                  "author": "Michael"
                }
                """.formatted(status);
    }
}
