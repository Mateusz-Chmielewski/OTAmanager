package com.mateuszchmielewski.otamanager.core.devices

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="device")
data class DeviceEntity (
    @Id
    @Column(name="id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name="name")
    val name: String,

    @Column(name="description")
    val description: String,

    @Column(name="group_id")
    val groupId: UUID,
)