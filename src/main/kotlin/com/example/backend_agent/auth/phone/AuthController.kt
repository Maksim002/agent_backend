package com.example.backend_agent.auth.phone

import com.example.backend_agent.bd.repository.AuthRepository
import com.example.utils.security.JwtUtil
import com.example.utils.component.ApiResponse
import org.springframework.http.HttpStatus

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class PhoneCheckRequest(val phoneNumber: String)
data class PasswordCheckRequest(val phoneNumber: String, val password: String)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepo: AuthRepository,
    private val jwtUtil: JwtUtil
) {

    // 1. Проверка существования номера
    @PostMapping("/check-phone")
    fun checkPhone(@RequestBody req: PhoneCheckRequest): ResponseEntity<ApiResponse<Boolean>> {
        val exists = userRepo.findByPhoneNumber(req.phoneNumber) != null
        return ResponseEntity.ok(ApiResponse(status = "SUCCESS", data = exists))
    }

    // 2. Проверка пароля и выдача токена
    @PostMapping("/check-password")
    fun checkPassword(@RequestBody req: PasswordCheckRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val user = userRepo.findByPhoneNumber(req.phoneNumber)
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse(status = "ERROR", data = null, message = "Пользователь не найден"))

        // Проверка пароля — здесь простая строка, в реальном приложении нужно хеширование!
        return if (user.password == req.password) {
            val token = jwtUtil.generateToken(user.phoneNumber, mapOf("fullName" to user.fullName))
            ResponseEntity.ok(ApiResponse(status = "SUCCESS", data = AuthResponse(token)))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse(status = "ERROR", data = null, message = "Неверный пароль"))
        }
    }
}