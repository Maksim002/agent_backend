package com.example.utils.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date
import java.util.UUID

@Component
class JwtUtil(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    private lateinit var key: Key
    private val validityMs = 1000L * 60 * 60 * 24  // 24 часа

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(username: String, claims: Map<String, Any>): String {
        val now = Date()
        val expiry = Date(now.time + validityMs)
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setId(UUID.randomUUID().toString()) // 👈 добавлен jti
            .addClaims(claims)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean =
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (_: Exception) {
            false
        }

    fun getUsername(token: String): String =
        Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token)
            .body
            .subject

    fun getJti(token: String): String = // 👈 вот этот метод добавлен
        Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token)
            .body
            .id

    fun getAllClaims(token: String): Claims =
        Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token)
            .body
}