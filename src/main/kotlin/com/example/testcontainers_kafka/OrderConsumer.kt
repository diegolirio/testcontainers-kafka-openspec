package com.example.testcontainers_kafka

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class OrderMessage(
    val id: UUID,
    val customerId: String,
    val createdAt: LocalDateTime,
    val productId: String,
    val amount: Int,
    val price: BigDecimal
)

@Service
class OrderConsumer(
    private val orderRepository: OrderRepository,
    private val orderProducer: OrderProducer
) {

    private val logger = LoggerFactory.getLogger(OrderConsumer::class.java)

    @KafkaListener(topics = ["orders"], groupId = "order-group")
    fun consume(message: OrderMessage) {
        logger.info("Consumed message: $message")
        val order = Order(
            id = message.id,
            customerId = message.customerId,
            createdAt = message.createdAt,
            productId = message.productId,
            amount = message.amount,
            price = message.price
        )
        val savedOrder = orderRepository.save(order)
        logger.info("Saved order to database: ${savedOrder.id}")
        
        orderProducer.sendMessage(savedOrder)
    }
}
