## Why

Integrating Kafka consumers into a Spring Boot application requires robust integration testing. Using Testcontainers ensures that these tests run against a real Kafka instance and database, providing higher confidence in the application's behavior. This change introduces an Order consumer that processes messages and persists them to a PostgreSQL database, managed by Flyway migrations, and verified by integration tests using Testcontainers.

## What Changes

- Add a `OrderConsumer` service to consume order messages from a Kafka topic.
- Implement an `Order` entity and JPA repository for database persistence.
- Implement Flyway database migrations to manage the schema.
- Implement a message listener that processes incoming JSON order messages and saves them to PostgreSQL.
- Add integration tests using Testcontainers to verify that orders are correctly persisted in the database.

## Capabilities

### New Capabilities
- `kafka-consumer`: Capability to consume order messages from a specified Kafka topic, process them, and persist them to a database.

### Modified Capabilities
- (none)

## Impact

- New service `OrderConsumer` and `Order` entity in `src/main/kotlin`.
- New repository `OrderRepository`.
- Flyway migration script in `src/main/resources/db/migration`.
- Updated `build.gradle.kts` with `spring-boot-starter-data-jpa` and `flyway-database-postgresql`.
- Updated test suite in `src/test/kotlin` to include Kafka/DB integration tests.
