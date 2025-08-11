package com.example.backend_agent.auth.phone

import com.example.backend_agent.bd.model.TokenBlacklist
import com.example.backend_agent.bd.repository.AuthRepository
import com.example.backend_agent.bd.repository.TokenBlacklistRepository
import com.example.utils.security.JwtUtil
import com.example.utils.component.ApiResponse
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// DTOs
data class PhoneCheckRequest(val phoneNumber: String)
data class PasswordCheckRequest(val phoneNumber: String, val password: String)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepo: AuthRepository,
    private val jwtUtil: JwtUtil,
    private val tokenBlacklistRepository: TokenBlacklistRepository
) {

    // 1. Проверка существования номера
    @PostMapping("/check-phone")
    fun checkPhone(@RequestBody req: PhoneCheckRequest): ResponseEntity<ApiResponse<Boolean>> {
        val exists = userRepo.findByPhoneNumber(req.phoneNumber) != null
        return ResponseEntity.ok(ApiResponse(status = "SUCCESS", data = exists))
    }

    // 2. Проверка пароля и выдача токена (с blacklist-очисткой старых)
    @PostMapping("/check-password")
    @Transactional
    fun checkPassword(@RequestBody req: PasswordCheckRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val user = userRepo.findByPhoneNumber(req.phoneNumber)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse(status = "ERROR", data = null, message = "Пользователь не найден"))

        if (user.password != req.password) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse(status = "ERROR", data = null, message = "Неверный пароль"))
        }

        // Удаляем все старые токены для пользователя
        tokenBlacklistRepository.deleteAllByUsername(user.phoneNumber)
        val token = jwtUtil.generateToken(user.phoneNumber, mapOf("fullName" to user.fullName))
        val jti = jwtUtil.getJti(token)

        // Сохраняем новый токен
        tokenBlacklistRepository.save(TokenBlacklist(jti = jti, username = user.phoneNumber))
        return ResponseEntity.ok(ApiResponse(status = "SUCCESS", data = AuthResponse(token)))
    }
}