package com.example.beckend_agent.auth.registration

import com.example.beckend_agent.bd.model.User
import com.example.beckend_agent.bd.repository.AuthRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class RegistrationController(
    private val phoneNumberRepository: AuthRepository
) {

    @PostMapping
    fun savePhoneNumber(@RequestBody user: User): ResponseEntity<Any> {
        val existing = phoneNumberRepository.findByPhoneNumber(user.phoneNumber)
        return if (existing != null) {
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Пользователь с таким номером уже существует.")
        } else {
            val saved = phoneNumberRepository.save(user)
            ResponseEntity.ok(saved)
        }
    }

    @GetMapping
    fun getAll(): List<User> = phoneNumberRepository.findAll()
}