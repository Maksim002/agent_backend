package com.example.utils.security

import com.example.backend_agent.bd.repository.TokenBlacklistRepository
import com.example.utils.component.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Primary
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService,
    private val tokenBlacklistRepository: TokenBlacklistRepository,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        println("Auth Header: $authHeader")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            if (jwtUtil.validateToken(token)) {
                val jti = jwtUtil.getJti(token)
                println("Token jti: $jti")

                // ✅ Меняем логику: разрешаем только токен, чей jti есть в БД
                if (!tokenBlacklistRepository.existsByJti(jti)) {
                    println("Token is NOT active: $jti")
                    sendUnauthorizedResponse(response, "Пользователь не авторизован")
                    return
                }

                val username = jwtUtil.getUsername(token)
                println("Authenticated username: $username")

                val userDetails = userDetailsService.loadUserByUsername(username)
                val auth = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
            } else {
                sendUnauthorizedResponse(response, "Невалидный токен")
            }
        } else {
            println("No Bearer token found")
        }

        filterChain.doFilter(request, response)
    }

    private fun sendUnauthorizedResponse(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"

        val apiResponse = ApiResponse<Unit>(
            status = "ERROR",
            data = null,
            message = message
        )

        val json = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(json)
        response.writer.flush()
    }
}