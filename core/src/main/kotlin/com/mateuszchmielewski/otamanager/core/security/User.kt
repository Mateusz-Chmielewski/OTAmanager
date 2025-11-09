package com.mateuszchmielewski.otamanager.core.security

data class
User(
    val id: Long,
    val username: String,
    val password: String,
)
