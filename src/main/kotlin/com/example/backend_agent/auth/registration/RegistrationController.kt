package com.example.backend_agent.auth.registration

import com.example.backend_agent.bd.model.User
import com.example.backend_agent.bd.repository.AuthRepository
import com.example.utils.component.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class RegistrationController(
    private val phoneNumberRepository: AuthRepository
) {

    @PostMapping
    fun savePhoneNumber(@RequestBody user: User): ResponseEntity<ApiResponse<User>> {
        val existing = phoneNumberRepository.findByPhoneNumber(user.phoneNumber)
        return if (existing != null) {
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                    ApiResponse(
                        status = "ERROR",
                        data = null,
                        message = "Пользователь с таким номером уже существует."
                    )
                )
        } else {
            val saved = phoneNumberRepository.save(user)
            ResponseEntity.ok(
                ApiResponse(
                    status = "SUCCESS",
                    data = saved,
                    message = null
                )
            )
        }
    }

    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<User>>> {
        val users = phoneNumberRepository.findAll()
        return ResponseEntity.ok(
            ApiResponse(
                status = "SUCCESS",
                data = users.ifEmpty { null }, // если пусто — будет null
                message = if (users.isEmpty()) "Нет пользователей." else null
            )
        )
    }
}