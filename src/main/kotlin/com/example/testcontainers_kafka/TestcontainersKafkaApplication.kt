package com.example.testcontainers_kafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestcontainersKafkaApplication

fun main(args: Array<String>) {
	runApplication<TestcontainersKafkaApplication>(*args)
}
