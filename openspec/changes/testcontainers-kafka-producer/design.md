## Context

The current system consumes orders from Kafka and persists them to a database. However, it lacks the ability to notify other systems about these events. We need to implement a Kafka producer that broadcasts these order events.

## Goals / Non-Goals

**Goals:**
- Implement a reliable Kafka producer for order events.
- Integrate the producer into the existing order processing flow.
- Ensure high test coverage using Testcontainers.

**Non-Goals:**
- Implementing retry mechanisms with dead-letter queues (out of scope for this initial phase).
- Modifying the incoming order message schema.

## Decisions

### 1. Use `KafkaTemplate` for Message Production
We will use Spring Kafka's `KafkaTemplate<String, Order>` to send messages. This provides a high-level API for interacting with Kafka and integrates well with existing Spring Boot configurations.

### 2. Service Injection in `OrderConsumer`
The `OrderConsumer` will be updated to depend on a new `OrderProducer` service. This separation of concerns ensures that the consumer focuses on message receipt and the producer on message broadcasting.

### 3. Dedicated Topic for Outgoing Events
A new topic named `order-events` will be used for outgoing notifications. This avoids confusion with the incoming `orders` topic and allows downstream consumers to subscribe specifically to processed events.

### 4. Integration Testing Strategy
We will create `OrderProducerIntegrationTest` using Testcontainers. The test will:
1. Start a Kafka container.
2. Trigger the production of an event.
3. Use a test consumer to verify the message was published to the `order-events` topic with the correct payload.

## Risks / Trade-offs

- **[Risk]**: Tight coupling between consumer and producer.
- **[Mitigation]**: Use an interface or a dedicated service for the producer to allow for easier mocking and decoupling in tests.
- **[Risk]**: Message loss if Kafka is down during production.
- **[Mitigation]**: For this phase, we accept the risk. Future iterations could include a transactional outbox pattern.
