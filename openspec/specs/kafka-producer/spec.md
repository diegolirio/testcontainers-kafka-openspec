# Kafka Producer Specification

This specification defines the requirements for the Kafka producer that publishes order events to a Kafka topic.

## Requirements

### Requirement: Order event publication
The system SHALL publish an order event to a Kafka topic named `order-events` whenever a new order is successfully persisted.

#### Scenario: Successful event publication
- **WHEN** an order is saved to the database
- **THEN** the system SHALL publish a JSON representation of that order to the `order-events` topic

### Requirement: Order event data format
The order event published to Kafka SHALL contain the following fields: `id`, `customerId`, `createdAt`, `productId`, `amount`, and `price`.

#### Scenario: Event payload verification
- **WHEN** an order event is published
- **THEN** the message payload SHALL be a JSON object containing all order fields

### Requirement: Producer integration testing
The system SHALL include integration tests that use Testcontainers to verify that order events are correctly produced to Kafka.

#### Scenario: Producer verification
- **WHEN** the integration test triggers an order creation
- **THEN** the test SHALL verify that a message is successfully published to the `order-events` topic
