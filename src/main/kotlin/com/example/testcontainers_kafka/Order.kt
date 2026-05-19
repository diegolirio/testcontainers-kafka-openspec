package com.example.testcontainers_kafka

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "orders")
data class Order(
    @Id
    val id: UUID,
    val customerId: String,
    val createdAt: LocalDateTime,
    val productId: String,
    val amount: Int,
    val price: BigDecimal
)
