package com.example.testcontainers_kafka

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<TestcontainersKafkaApplication>().with(TestcontainersConfiguration::class).run(*args)
}
