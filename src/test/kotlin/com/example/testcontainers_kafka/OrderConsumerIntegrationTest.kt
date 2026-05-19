package com.example.testcontainers_kafka

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
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class OrderConsumerIntegrationTest {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var kafkaContainer: org.testcontainers.kafka.KafkaContainer

    @Test
    fun `should consume order from kafka, save to database and notify the world`() {
        // Given
        val orderId = UUID.randomUUID()
        val orderMessage = OrderMessage(
            id = orderId,
            customerId = "customer-123",
            createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            productId = "product-456",
            amount = 2,
            price = BigDecimal("99.99")
        )

        // Setup test consumer for 'order-events'
        val consumerProps = mutableMapOf<String, Any>(
            org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
            org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG to "test-group-${UUID.randomUUID()}",
            org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
        )

        val consumerFactory = DefaultKafkaConsumerFactory(
            consumerProps,
            StringDeserializer(),
            JsonDeserializer(Order::class.java).apply {
                addTrustedPackages("com.example.testcontainers_kafka")
                ignoreTypeHeaders()
            }
        )
        val testConsumer = consumerFactory.createConsumer()
        testConsumer.subscribe(listOf("order-events"))

        // When
        kafkaTemplate.send("orders", orderId.toString(), orderMessage)

        // Then - Verify Database
        await.atMost(10, TimeUnit.SECONDS) untilAsserted {
            val orderOptional = orderRepository.findById(orderId)
            assertTrue(orderOptional.isPresent, "Order should be present in database")
            val savedOrder = orderOptional.get()
            assertEquals(orderMessage.customerId, savedOrder.customerId)
            assertEquals(orderMessage.productId, savedOrder.productId)
            assertEquals(orderMessage.amount, savedOrder.amount)
            assertEquals(orderMessage.price, savedOrder.price)
        }

        // Then - Verify Outbound Kafka Notification
        await.atMost(10, TimeUnit.SECONDS) untilAsserted {
            val records = testConsumer.poll(Duration.ofMillis(100))
            val event = records.find { it.key() == orderId.toString() }
            assertTrue(event != null, "Order event should be published to order-events")
            val publishedOrder = event.value()
            assertEquals(orderId, publishedOrder.id)
            assertEquals(orderMessage.customerId, publishedOrder.customerId)
        }

        testConsumer.close()
    }
}

