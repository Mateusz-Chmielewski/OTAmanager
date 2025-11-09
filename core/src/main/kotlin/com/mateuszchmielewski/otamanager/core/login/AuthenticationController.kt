package com.mateuszchmielewski.otamanager.core.login

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RequestMapping("/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    data class LoginRequest(val username: String, val password: String)
    data class LoginResponse(val token: String)

    @PostMapping("/login")
    fun login(@RequestBody body : LoginRequest): LoginResponse {
        val token = authenticationService.login(body.username, body.password)
        return LoginResponse(token)
    }
}