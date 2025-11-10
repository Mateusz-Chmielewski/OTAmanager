package com.mateuszchmielewski.otamanager.core.devices

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DeviceRepository : JpaRepository<DeviceEntity, UUID> {
}