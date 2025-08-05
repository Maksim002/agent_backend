package com.example.backend_agent.bd.repository

import com.example.backend_agent.bd.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface AuthRepository : JpaRepository<User, Long> {
    fun findByPhoneNumber(user: String): User?
}