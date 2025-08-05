package com.example.beckend_agent.auth.registration

import com.example.beckend_agent.bd.model.User
import com.example.beckend_agent.bd.repository.AuthRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class RegistrationController(
    private val phoneNumberRepository: AuthRepository
) {

    @PostMapping
    fun savePhoneNumber(@RequestBody user: User): User {
        return phoneNumberRepository.save(user)
    }

    @GetMapping
    fun getAll(): List<User> = phoneNumberRepository.findAll()
}