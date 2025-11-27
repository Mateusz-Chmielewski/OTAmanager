package com.mateuszchmielewski.otamanager.core.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret}") private var jwtSecret: String,
    @Value("\${jwt.expiration}") private var jwtExpirationMs: Long = 0
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))

    private fun generateToken(username: String, type: String, expiry: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(username)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String): String {
        return generateToken(userId, "access", jwtExpirationMs)
    }

//    fun generateRefreshToken(userId: String): String {
//        return generateToken(userId, "refresh", jwtExpirationMs)
//    }


    fun validateToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val expiration = claims.expiration
        return !expiration.before(Date())
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = getRawToken(token)
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch(e: Exception) {
            null
        }
    }

    private fun getRawToken(token: String): String {
        if (token.startsWith("Bearer ")) {
            return token.removePrefix("Bearer ")
        }
        return token
    }

    fun getUsernameFromToken(authHeader: String): String {
        val claims = parseAllClaims(authHeader)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Token")
        return claims.subject
    }
}