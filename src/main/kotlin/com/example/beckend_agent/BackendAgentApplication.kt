package com.example.beckend_agent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AgentBackendApplication

fun main(args: Array<String>) {
    runApplication<AgentBackendApplication>(*args)
}
