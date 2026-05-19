# Kafka Consumer Specification

This specification defines the requirements for the Kafka consumer that processes order messages and persists them to a PostgreSQL database.

## Requirements

### Requirement: Order message consumption
The system SHALL consume messages from a Kafka topic named `orders`. The message payload SHALL be a JSON representing an order.

#### Scenario: Successful order message consumption
- **WHEN** a valid JSON order message is published to the `orders` topic
- **THEN** the `OrderConsumer` SHALL receive the message

### Requirement: Order data structure
The system SHALL support order data with the following fields: `id`, `customerId`, `createdAt`, `productId`, `amount`, and `price`.

#### Scenario: Data field mapping
- **WHEN** a message with fields `id`, `customerId`, `createdAt`, `productId`, `amount`, and `price` is received
- **THEN** the system SHALL correctly map these fields to the internal `Order` model

### Requirement: Database schema migration
The system SHALL manage the database schema using Flyway migrations.

#### Scenario: Schema initialization
- **WHEN** the application starts
- **THEN** Flyway SHALL apply the migration scripts to create the `orders` table

### Requirement: Database persistence
The system SHALL persist consumed orders into a PostgreSQL database using Spring Data JPA.

#### Scenario: Persisting an order
- **WHEN** an order is consumed from Kafka
- **THEN** a record SHALL be inserted into the `orders` table with all message fields

### Requirement: Integration testing with Testcontainers
The system SHALL include integration tests that use Testcontainers to verify the full flow from Kafka message publication to database persistence.

#### Scenario: End-to-end verification
- **WHEN** the integration tests publish an order message to Kafka
- **THEN** the system SHALL verify that a corresponding record exists in the PostgreSQL database

### Requirement: Trigger order notification
The `OrderConsumer` SHALL trigger a notification to the external world after successfully persisting an order.

#### Scenario: Post-persistence notification
- **WHEN** an order has been successfully saved to the database
- **THEN** the system SHALL invoke the order notification mechanism (Kafka producer)
