package com.example.beckend_agent.auth.registration

import com.example.beckend_agent.bd.model.User
import com.example.beckend_agent.bd.repository.AuthRepository
import com.example.utils.ApiResponse
import org.springframework.dao.DataIntegrityViolationException
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
            try {
                val saved = phoneNumberRepository.save(user)
                ResponseEntity.ok(
                    ApiResponse(
                        status = "SUCCESS",
                        data = saved,
                        message = null
                    )
                )
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                        ApiResponse(
                            status = "ERROR",
                            data = null,
                            message = "Номер телефона уже зарегистрирован."
                        )
                    )
            }
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