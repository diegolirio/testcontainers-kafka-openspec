package com.example.testcontainers_kafka

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.kafka.outbound-topic:order-events}") private val topic: String
) {
    private val logger = LoggerFactory.getLogger(OrderProducer::class.java)

    fun sendMessage(order: Order) {
        logger.info("Producing message to topic $topic: $order")
        kafkaTemplate.send(topic, order.id.toString(), order)
            .whenComplete { result, ex ->
                if (ex == null) {
                    logger.info("Message sent successfully to topic $topic")
                } else {
                    logger.error("Failed to send message to topic $topic", ex)
                }
            }
    }
}
