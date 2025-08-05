package com.example.beckend_agent.bd.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "full_name", nullable = false)
    lateinit var fullName: String

    @Column(name = "phone_number", unique = true, nullable = false)
    lateinit var phoneNumber: String

    // Дополнительный конструктор для удобства
    constructor(fullName: String, phoneNumber: String) : this() {
        this.fullName = fullName
        this.phoneNumber = phoneNumber
    }
}