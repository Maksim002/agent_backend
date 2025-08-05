package com.example.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories("com.example.backend_agent.bd.repository")
@EntityScan("com.example.backend_agent.bd.model")
@SpringBootApplication(scanBasePackages = ["com.example.utils.security"])
class AgentBackendApplication

fun main(args: Array<String>) {
    runApplication<AgentBackendApplication>(*args)
}
