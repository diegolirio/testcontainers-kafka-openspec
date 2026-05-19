## Why

After an order is successfully created and persisted in the system, other applications in the ecosystem need to be notified of this event to trigger downstream processes (e.g., shipping, notifications, analytics). Introducing a Kafka producer ensures reliable, asynchronous communication of order events.

## What Changes

- **New Kafka Producer**: Implementation of a service to publish order events to a Kafka topic.
- **Order Notification Logic**: Update the order processing flow to trigger a notification after successful persistence.
- **Integration Tests**: Comprehensive tests using Testcontainers to verify that orders are correctly published to Kafka.

## Capabilities

### New Capabilities
- `kafka-producer`: Handles the publishing of order events to the `order-events` Kafka topic.

### Modified Capabilities
- `kafka-consumer`: The consumer logic might be updated to ensure the full end-to-end flow is respected, although primarily this is a new producer capability.

## Impact

- `OrderConsumer.kt`: Will likely be updated to call the new producer after saving an order.
- `src/main/kotlin/.../OrderProducer.kt`: New service for Kafka messaging.
- `src/test/kotlin/.../OrderProducerIntegrationTest.kt`: New integration test.
- `application.properties`: New Kafka producer configurations.
