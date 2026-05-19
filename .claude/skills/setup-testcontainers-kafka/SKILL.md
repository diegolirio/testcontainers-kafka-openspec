---
name: setup-testcontainers-kafka
description: Set up Testcontainers + Kafka integration tests in a Kotlin Spring Boot project. Generates TestcontainersConfiguration, integration tests with Awaitility for Consumers and/or Producers, and adds required dependencies to build.gradle.kts.
license: MIT
metadata:
  author: project
  version: "1.0"
---

Set up Testcontainers + Kafka integration tests in a Kotlin Spring Boot project.

## Overview

This skill scaffolds everything needed to test Kafka Consumers and Producers with real containers
(Apache Kafka + PostgreSQL), using Spring Boot's `@ServiceConnection`, `@SpringBootTest`, and
`Awaitility` for async assertions. It follows the exact patterns proven in this project.

---

## Steps

### 1. Gather context

Run the following in parallel to understand the project state:

```bash
find src/main/kotlin -name "*.kt" | sort
find src/test/kotlin -name "*.kt" | sort
cat build.gradle.kts
cat src/main/resources/application.properties
```

Parse the output to identify:
- **Package name** — base package of the application (e.g. `com.example.myapp`)
- **Domain entity** — the main JPA `@Entity` class (name, fields, table)
- **Consumer class** — class annotated with `@KafkaListener` (topic name, input message type)
- **Producer class** — class that uses `KafkaTemplate` (outbound topic name)
- **TestcontainersConfiguration** — whether `src/test/kotlin/.../TestcontainersConfiguration.kt` already exists
- **Existing integration tests** — whether any `*IntegrationTest.kt` already exists

### 2. Ask the user

Use the **AskUserQuestion** tool to ask:

```
Which Kafka roles does your project have?
```

Options (multiSelect: true):
- **Consumer** — has `@KafkaListener`, reads from a topic and persists / processes messages
- **Producer** — has a service that sends messages via `KafkaTemplate` to an outbound topic

> If neither is detected from the code scan, present both options so the user can choose.
> If both are already detected, pre-select both and confirm with the user.

Also ask (single select):
```
Does your consumer persist data to PostgreSQL?
```
Options:
- **Yes, uses PostgreSQL** — `PostgreSQLContainer` + Flyway will be included
- **No, Kafka only** — only `KafkaContainer` will be set up

### 3. Check and add dependencies

Open `build.gradle.kts`. Verify each dependency is present; add any that are missing.

**Required dependencies (if not present):**

```kotlin
// build.gradle.kts — testImplementation section
testImplementation("org.springframework.boot:spring-boot-testcontainers")
testImplementation("org.testcontainers:testcontainers-junit-jupiter")
testImplementation("org.testcontainers:testcontainers-kafka")
testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
```

If PostgreSQL is included:
```kotlin
testImplementation("org.testcontainers:testcontainers-postgresql")
```

If `flyway-core` is used in main but `flyway-database-postgresql` is missing:
```kotlin
implementation("org.flywaydb:flyway-database-postgresql")
```

**Do NOT add duplicates.** Only append lines that are truly absent.

### 4. Generate `TestcontainersConfiguration.kt` (if missing)

If `TestcontainersConfiguration.kt` does not exist, create it at:
`src/test/kotlin/<package>/TestcontainersConfiguration.kt`

**Template — Kafka + PostgreSQL:**

```kotlin
package <package>

import org.flywaydb.core.Flyway
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun kafkaContainer(): KafkaContainer {
        return KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"))
    }

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
    }

    @Bean
    fun flyway(dataSource: DataSource): Flyway {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
        flyway.migrate()
        return flyway
    }
}
```

**Template — Kafka only (no PostgreSQL):**

```kotlin
package <package>

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun kafkaContainer(): KafkaContainer {
        return KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"))
    }
}
```

> If `TestcontainersConfiguration.kt` already exists, do NOT overwrite it. Skip this step and use
> the existing class in the `@Import` annotations below.

### 5. Generate the integration test(s)

Determine which tests to generate based on the user's selections in Step 2.

---

#### 5A. Consumer integration test

**When to generate:** User selected **Consumer**.

**File:** `src/test/kotlin/<package>/<Entity>ConsumerIntegrationTest.kt`

**What the test does:**
1. Publishes a message to the consumer's inbound topic via `KafkaTemplate`
2. Waits with `Awaitility` until the record appears in the database (if PostgreSQL) or until a
   verifiable side-effect occurs
3. Asserts all persisted fields match the published payload

**Template:**

```kotlin
package <package>

import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class <Entity>ConsumerIntegrationTest {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    private lateinit var <entityRepository>: <Entity>Repository   // inject repo if PostgreSQL

    @Test
    fun `should consume <entity> from kafka and save to database`() {
        // Given
        val id = UUID.randomUUID()
        val message = <MessageType>(
            id = id,
            // populate fields from the detected message/entity type
        )

        // When
        kafkaTemplate.send("<inbound-topic>", id.toString(), message)

        // Then
        await.atMost(10, TimeUnit.SECONDS) untilAsserted {
            val record = <entityRepository>.findById(id)
            assertTrue(record.isPresent, "<Entity> should be present in database")
            val saved = record.get()
            // assert each field
        }
    }
}
```

Replace all `<...>` tokens with the actual names discovered in Step 1.

---

#### 5B. Producer integration test

**When to generate:** User selected **Producer**.

**What the test does:**
1. Triggers the production flow (e.g. calls the producer service directly, or publishes to the
   inbound topic and lets the consumer chain fire the producer)
2. Creates a manual `KafkaConsumer` subscribed to the outbound topic
3. Waits with `Awaitility` until the expected message appears in that topic
4. Asserts message key and payload fields

**Template:**

```kotlin
package <package>

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.testcontainers.kafka.KafkaContainer
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class <Entity>ProducerIntegrationTest {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    private lateinit var kafkaContainer: KafkaContainer

    @Test
    fun `should produce <entity> event to outbound topic`() {
        // Given
        val id = UUID.randomUUID()
        val message = <MessageType>(
            id = id,
            // populate fields
        )

        // Set up test consumer on outbound topic
        val consumerProps = mutableMapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "test-group-${UUID.randomUUID()}",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
        )
        val consumerFactory = DefaultKafkaConsumerFactory(
            consumerProps,
            StringDeserializer(),
            JsonDeserializer(<Entity>::class.java).apply {
                addTrustedPackages("<package>")
                ignoreTypeHeaders()
            }
        )
        val testConsumer = consumerFactory.createConsumer()
        testConsumer.subscribe(listOf("<outbound-topic>"))

        // When — trigger the inbound flow so the producer fires
        kafkaTemplate.send("<inbound-topic>", id.toString(), message)

        // Then — verify outbound message
        await.atMost(10, TimeUnit.SECONDS) untilAsserted {
            val records = testConsumer.poll(Duration.ofMillis(100))
            val event = records.find { it.key() == id.toString() }
            assertTrue(event != null, "<Entity> event should be published to <outbound-topic>")
            assertEquals(id, event.value().id)
            // assert other fields
        }

        testConsumer.close()
    }
}
```

Replace all `<...>` tokens with the actual names discovered in Step 1.

---

#### 5C. Combined Consumer + Producer test (recommended when both are selected)

**When to generate:** User selected **both Consumer and Producer**.

Instead of two separate test files, generate a **single** integration test that verifies the full
end-to-end flow in one test method:

1. Publish to inbound topic
2. Assert record in DB (Awaitility)
3. Assert message on outbound topic (Awaitility + manual consumer)

This is the pattern proven in `OrderConsumerIntegrationTest.kt` in this project.

Use the `@Import(TestcontainersConfiguration::class) @SpringBootTest` structure with:
- `kafkaTemplate` for sending
- repository for DB assertion
- `kafkaContainer` (injected bean) to build the manual consumer's bootstrap config

---

### 6. Verify and report

After generating files, run:

```bash
./gradlew test --tests "*IntegrationTest*" 2>&1 | tail -30
```

If tests pass, report success. If they fail:
- Show the relevant error from the output
- Diagnose (wrong topic name, missing trusted packages, field mismatch, serialization issue)
- Fix and re-run once

### 7. Final output

```
## Testcontainers + Kafka Integration Tests — Done

### Files created / modified
- [new] src/test/kotlin/<package>/TestcontainersConfiguration.kt   (if was missing)
- [new] src/test/kotlin/<package>/<Entity>ConsumerIntegrationTest.kt  (if Consumer selected)
- [new] src/test/kotlin/<package>/<Entity>ProducerIntegrationTest.kt  (if Producer selected)
- [mod] build.gradle.kts  (added missing testImplementation deps)

### Test results
✓ <test name> — PASSED

### What was set up
- KafkaContainer (apache/kafka-native:latest) via @ServiceConnection
- PostgreSQLContainer + Flyway bean   (if PostgreSQL selected)
- Awaitility await.atMost(10s) for async Consumer verification
- Manual DefaultKafkaConsumerFactory to verify outbound Producer messages
```

---

## Key patterns (do not deviate)

| Concern | Pattern |
|---|---|
| Container wiring | `@TestConfiguration` + `@ServiceConnection` — NO manual property overrides |
| Kafka image | `apache/kafka-native:latest` |
| PostgreSQL image | `postgres:latest` |
| Flyway in tests | explicit `@Bean fun flyway(dataSource)` in `TestcontainersConfiguration` |
| Async assertion | `await.atMost(10, TimeUnit.SECONDS) untilAsserted { ... }` (awaitility-kotlin DSL) |
| Outbound verification | `DefaultKafkaConsumerFactory` + manual `poll()` inside `untilAsserted` |
| Test consumer group | `"test-group-${UUID.randomUUID()}"` — unique per test run to avoid offset conflicts |
| Deserializer trust | `.addTrustedPackages("<package>")` + `.ignoreTypeHeaders()` |
| Test class annotation | `@Import(TestcontainersConfiguration::class)` + `@SpringBootTest` |
| `kafkaContainer` bean | Inject `org.testcontainers.kafka.KafkaContainer` directly when building manual consumer |

## Guardrails

- Never overwrite an existing `TestcontainersConfiguration.kt`
- Never add duplicate dependencies to `build.gradle.kts`
- Never use `Thread.sleep` — always use Awaitility
- Never hardcode bootstrap servers — always read from the injected `KafkaContainer` bean
- Infer entity/topic/package names from the actual source code; only ask the user when ambiguous
- If the user says "only Consumer" do not generate a Producer test and vice-versa
