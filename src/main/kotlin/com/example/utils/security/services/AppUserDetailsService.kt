package com.example.utils.security.services

import com.example.backend_agent.bd.repository.AuthRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserDetailsService(
    private val userRepository: AuthRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val u = userRepository.findByPhoneNumber(username)
            ?: throw UsernameNotFoundException("User not found")
        // Нет пароля и ролей, создаём UserDetails с пустым списком authorities
        return User(u.phoneNumber, "", emptyList())
    }
}