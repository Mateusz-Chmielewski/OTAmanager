package com.mateuszchmielewski.otamanager.core.firmware

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="firmware")
data class FirmwareEntity (
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    @Column(name="device_id")
    val deviceId: UUID,

    @Column(name="description")
    val description: String,
)