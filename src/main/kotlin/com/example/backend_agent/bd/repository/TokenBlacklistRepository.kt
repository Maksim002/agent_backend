package com.example.backend_agent.bd.repository

import com.example.backend_agent.bd.model.TokenBlacklist
import org.springframework.data.jpa.repository.JpaRepository

interface TokenBlacklistRepository : JpaRepository<TokenBlacklist, Long> {
    fun existsByJti(jti: String): Boolean
    fun deleteAllByUsername(username: String)
}