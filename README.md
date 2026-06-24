# Documentation Platform

Java 21 Spring Boot foundation for managing engineering documentation.

## Scope

Implemented for V1:

- PostgreSQL schema managed by Flyway
- REST APIs under `/api/documents`
- GraphQL APIs under `/graphql`
- Document CRUD operations
- Case-insensitive keyword search across title and content
- Filters for document type, status, and author
- Pagination and `createdAt` descending sorting
- Validation, global REST exception handling, Actuator health, and Swagger UI

Explicitly not implemented: semantic search, vector search, embeddings, RAG, chatbot, Elasticsearch, Redis, caching, authentication, or authorization.

## Run Locally

Start PostgreSQL with a `documentation_platform` database and credentials matching `src/main/resources/application.yml`, or override:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/documentation_platform \
SPRING_DATASOURCE_USERNAME=docs \
SPRING_DATASOURCE_PASSWORD=docs \
mvn spring-boot:run
```

The app also imports a local `.env` file from the project root when present, so IDE runs and `mvn spring-boot:run` can share the same database settings.

You can also run only PostgreSQL with Docker and keep the Spring app local:

```bash
docker compose up -d postgres
mvn spring-boot:run
```

## Run With Docker

Build and run PostgreSQL plus the Spring application:

```bash
docker compose up --build
```

Run in the background:

```bash
docker compose up --build -d
```

Stop the containers:

```bash
docker compose down
```

Remove the PostgreSQL volume as well:

```bash
docker compose down -v
```

Useful endpoints:

- REST: `http://localhost:8080/api/documents`
- GraphQL: `http://localhost:8080/graphql`
- GraphiQL: `http://localhost:8080/graphiql`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

## Test

```bash
mvn test
```

Integration tests use Testcontainers with PostgreSQL.
