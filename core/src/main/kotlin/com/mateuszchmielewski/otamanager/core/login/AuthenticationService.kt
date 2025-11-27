package com.mateuszchmielewski.otamanager.core.login

import com.mateuszchmielewski.otamanager.core.security.JwtService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val jwtService: JwtService
) {
    fun login(username: String, password: String): String {
        if (username == "admin" && password == "password") {
            val newAccessToken = jwtService.generateAccessToken(username)
            return newAccessToken
        } else {
            throw BadCredentialsException("Invalid credentials")
        }
    }
}