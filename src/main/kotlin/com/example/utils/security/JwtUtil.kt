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

@Component
class JwtUtil(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    private lateinit var key: Key
    private val validityMs = 1000L * 60 * 60 * 24  // 24 часа

    @PostConstruct
    fun init() {
        // Secret гарантированно не null и достаточной длины
        key = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(username: String, claims: Map<String, Any>): String =
        Jwts.builder()
            .setSubject(username)
            .addClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + validityMs))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

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

    fun getAllClaims(token: String): Claims =
        Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token)
            .body
}