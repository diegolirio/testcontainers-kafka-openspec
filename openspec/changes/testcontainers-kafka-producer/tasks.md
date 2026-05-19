## 1. Producer Implementation

- [x] 1.1 Create `OrderProducer` service in `src/main/kotlin/.../testcontainers_kafka/`
- [x] 1.2 Implement `sendMessage(order: Order)` using `KafkaTemplate`
- [x] 1.3 Configure JSON serialization for the producer in `application.properties`

## 2. Consumer Integration

- [x] 2.1 Update `OrderConsumer` to inject `OrderProducer`
- [x] 2.2 Call `orderProducer.sendMessage(savedOrder)` after successful database save in `OrderConsumer.receive()`

## 3. Configuration

- [x] 3.1 Add Kafka producer properties to `src/main/resources/application.properties`
- [x] 3.2 Define the `order-events` topic name as a configuration property

## 4. Testing

- [x] 4.1 Update `OrderConsumerIntegrationTest` to include a test consumer for the `order-events` topic
- [x] 4.2 Expand the existing test case: After verifying the DB record, verify that a message is successfully published to the `order-events` topic
- [x] 4.3 Run all tests to ensure the full end-to-end flow is verified in a single integration test
