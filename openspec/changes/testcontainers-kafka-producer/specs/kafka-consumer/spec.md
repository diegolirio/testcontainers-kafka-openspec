## ADDED Requirements

### Requirement: Trigger order notification
The `OrderConsumer` SHALL trigger a notification to the external world after successfully persisting an order.

#### Scenario: Post-persistence notification
- **WHEN** an order has been successfully saved to the database
- **THEN** the system SHALL invoke the order notification mechanism (Kafka producer)
