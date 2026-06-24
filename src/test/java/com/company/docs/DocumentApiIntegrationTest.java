package com.company.docs;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.company.docs.dto.CreateDocumentRequest;
import com.company.docs.entity.DocumentStatus;
import com.company.docs.entity.DocumentType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class DocumentApiIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("documentation_platform")
            .withUsername("docs")
            .withPassword("docs");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsRetrievesAndSearchesDocument() throws Exception {
        String id = createDocument("Payment Service Deployment", DocumentType.DEPLOYMENT, DocumentStatus.DRAFT, "Michael");

        mockMvc.perform(get("/api/documents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.docType", is("DEPLOYMENT")))
                .andExpect(jsonPath("$.author", is("Michael")));

        mockMvc.perform(get("/api/documents/search")
                        .param("keyword", "payment")
                        .param("docType", "DEPLOYMENT")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(id)));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        CreateDocumentRequest request = new CreateDocumentRequest(
                "",
                DocumentType.REQUIREMENT,
                "Content",
                DocumentStatus.DRAFT,
                "Michael"
        );

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Request validation failed")));
    }

    @Test
    void supportsGraphqlDocumentQuery() throws Exception {
        String id = createDocument("Redis Development Notes", DocumentType.DEVELOPMENT, DocumentStatus.PUBLISHED, "Avery");
        String query = """
                query($id: ID!) {
                  document(id: $id) {
                    id
                    title
                    docType
                    status
                    author
                  }
                }
                """;

        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "query", query,
                                "variables", Map.of("id", id)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.document.id", is(id)))
                .andExpect(jsonPath("$.data.document.docType", is("DEVELOPMENT")))
                .andExpect(jsonPath("$.data.document.status", is("PUBLISHED")));
    }

    @Test
    void exposesHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    private String createDocument(String title, DocumentType type, DocumentStatus status, String author) throws Exception {
        CreateDocumentRequest request = new CreateDocumentRequest(
                title,
                type,
                "Deployment instructions include PostgreSQL migrations.",
                status,
                author
        );

        MvcResult result = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asText();
    }
}
