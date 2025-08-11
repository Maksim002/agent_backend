package com.example.backend_agent.bd.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "token_blacklist")
class TokenBlacklist(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var jti: String = "",

    @Column(nullable = false)
    var username: String = ""

) {
    // Обязательный пустой конструктор для JPA
    constructor() : this(null, "", "")
}