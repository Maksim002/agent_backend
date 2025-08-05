package com.example.beckend_agent.bd.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["phone_number"])])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "full_name")
    val fullName: String,

    @Column(name = "phone_number", unique = true)
    val phoneNumber: String
)