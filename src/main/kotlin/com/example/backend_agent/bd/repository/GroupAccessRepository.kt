package com.example.backend_agent.bd.repository

import com.example.backend_agent.bd.model.Group
import com.example.backend_agent.bd.model.GroupAccess
import com.example.backend_agent.bd.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface GroupAccessRepository : JpaRepository<GroupAccess, Long> {
    fun findByUser(user: User): List<GroupAccess>
    fun findByGroupAndUser(group: Group, user: User): GroupAccess?
}