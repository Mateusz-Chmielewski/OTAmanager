package com.mateuszchmielewski.otamanager.core.logs

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity(name="log")
data class LogEntity (
    @Id()
    @Column(name="id")
    val id: UUID,

    @Column(name="device_id")
    val deviceId: UUID,

    @Column(name="timestamp")
    val timestamp: Long,

    @Column(name="event_type")
    val eventType: String,

    @Column(name="message")
    val message: String,

)