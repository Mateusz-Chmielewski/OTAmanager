package com.mateuszchmielewski.otamanager.core.firmware

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name="firmware")
data class FirmwareEntity (
    @Id
    @Column(name="id")
//    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name="group_id")
    val groupId: UUID,

    @Column(name="version")
    val version: String,

    @Column(name="description")
    val description: String,

    @Column(name="uploaded_at")
    val uploadedAt: Date = Date(),

    @Column(name="is_active")
    val isActive: Boolean = false,
)