## 1. Setup & Dependencies

- [x] 1.1 Add `spring-boot-starter-data-jpa` to `build.gradle.kts`
- [x] 1.2 Add `flyway-core` and `flyway-database-postgresql` to `build.gradle.kts`
- [x] 1.3 Add `awaitility` to `build.gradle.kts` (testImplementation)

## 2. Persistence Layer & Migrations

- [x] 2.1 Create Flyway migration `V1__create_orders_table.sql` in `src/main/resources/db/migration`
- [x] 2.2 Create `Order` entity with fields: `id`, `customerId`, `createdAt`, `productId`, `amount`, `price`
- [x] 2.3 Create `OrderRepository` interface

## 3. Consumer Implementation

- [x] 3.1 Create `OrderConsumer` service
- [x] 3.2 Implement `@KafkaListener` for topic `orders`
- [x] 3.3 Implement logic to save the consumed `Order` via `OrderRepository`

## 4. Configuration

- [x] 4.1 Update `application.properties` with Kafka consumer, JPA, and Flyway settings

## 5. Testing

- [x] 5.1 Create `OrderConsumerIntegrationTest`
- [x] 5.2 Configure test to use `TestcontainersConfiguration`
- [x] 5.3 Implement test case: Publish order JSON -> Wait with Awaitility -> Assert record in DB
- [x] 5.4 Run tests and verify success
