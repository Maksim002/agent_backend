package com.example.backend_agent.bd.repository

import com.example.backend_agent.bd.model.Group
import com.example.backend_agent.bd.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Long> {
    fun findByOwner(owner: User): List<Group>
    fun findByOwnerAndName(owner: User, name: String): Group?
}