## Context

The current project is a Spring Boot application with Testcontainers support for Kafka and PostgreSQL. We need to implement a Kafka consumer that processes order information and saves it to a database, ensuring reliability through integration tests and versioned database migrations.

## Goals / Non-Goals

**Goals:**
- Implement `OrderConsumer` with `@KafkaListener`.
- Define `Order` entity with fields: `id`, `customerId`, `createdAt`, `productId`, `amount`, `price`.
- Implement `OrderRepository` for PostgreSQL persistence.
- Manage database schema with Flyway migrations.
- Create an integration test that verifies database state after message consumption.

**Non-Goals:**
- Implementing complex business logic or validation (focus on flow).
- Custom Kafka producer implementation (tests will use `KafkaTemplate`).

## Decisions

### Decision: Use `@KafkaListener` and JSON Deserialization
**Rationale:** Spring Kafka's built-in JSON support simplifies mapping Kafka payloads to Kotlin objects.
**Alternatives Considered:** Manual byte array manipulation.

### Decision: Use Spring Data JPA with PostgreSQL
**Rationale:** Standard way to persist data in Spring Boot. Matches the existing `PostgreSQLContainer` in tests.

### Decision: Use Flyway for Database Migrations
**Rationale:** Versioned migrations ensure database schema consistency across environments (dev, test, prod).
**Alternatives Considered:** Hibernate ddl-auto (not recommended for production).

### Decision: Use `Awaitility` for Async Testing
**Rationale:** Kafka consumption is asynchronous. `Awaitility` allows the test to wait for the database record to appear without fixed sleep times.

## Risks / Trade-offs

- **[Risk]** Test instability due to timing. → **Mitigation** Use `Awaitility` with a reasonable timeout (e.g., 5-10 seconds).
- **[Risk]** JPA/Database overhead in tests. → **Mitigation** Use `@DataJpaTest` or reuse containers via `@ServiceConnection`.
