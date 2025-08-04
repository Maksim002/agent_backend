package com.example.beckend_agent.bd.repository

import com.example.beckend_agent.bd.model.PhoneNumber
import org.springframework.data.jpa.repository.JpaRepository

interface PhoneNumberRepository : JpaRepository<PhoneNumber, Long>