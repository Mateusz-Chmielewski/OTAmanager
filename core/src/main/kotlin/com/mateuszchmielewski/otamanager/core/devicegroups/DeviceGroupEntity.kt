package com.mateuszchmielewski.otamanager.core.devicegroups

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity()
data class DeviceGroupEntity(
    @Id
    @Column(name="id")
    val id: UUID = UUID.randomUUID(),

    @Column(name="name")
    val name: String,

    @Column(name="description")
    val description: String,

    @Column(name="user_id")
    val userId: Long,
)